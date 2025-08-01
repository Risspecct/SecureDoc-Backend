package users.rishik.SecureDoc.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Config.FileStorageProperties;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Projections.FileView;
import users.rishik.SecureDoc.Repositories.FileRepository;
import users.rishik.SecureDoc.Repositories.UserRepository;
import users.rishik.SecureDoc.Security.Service.SecurityService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FileStorageProperties storage;
    private final SecurityService securityService;
    private final UserRepository userRepository;

    FileService(FileRepository fileRepository, FileStorageProperties storage, SecurityService securityService, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.storage = storage;
        this.securityService = securityService;
        this.userRepository = userRepository;
    }

    // Add File
    public void uploadFile(MultipartFile file, Roles accessLevel) throws IOException {
        Roles currentUserRole = getCurrentUser().getRole();

        if (file.isEmpty()) throw new IllegalArgumentException("Empty File submitted");
        Path targetPath = storage.getUploadPath().resolve(Objects.requireNonNull(file.getOriginalFilename())).normalize();
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        if (accessLevel == null) accessLevel = currentUserRole;
        if (currentUserRole.getLevel() >= accessLevel.getLevel())
            saveMetaData(file, accessLevel);
        else throw new AccessDeniedException("You can only upload files with access level " + currentUserRole + " or lower");

        log.info("File {} uploaded by user: {}", file.getOriginalFilename(), getCurrentUser().getEmail());
    }

    public void saveMetaData(MultipartFile file, Roles accessLevel){
        FileMetaData metaData = new FileMetaData();

        metaData.setOriginalName(file.getOriginalFilename());
        metaData.setFileName(file.getName());
        metaData.setSize(file.getSize());
        metaData.setCreatedAt(LocalDateTime.now());
        metaData.setContentType(file.getContentType());
        metaData.setOwner(securityService.getCurrentUser().getUsername());
        metaData.setAccessLevel(accessLevel);
        metaData.setAccessWeight(accessLevel.getLevel());
        metaData.setTeam(getCurrentUser().getTeam());
        metaData.setUser(userRepository.findById(securityService.getCurrentUser().getId())
                .orElseThrow(() -> new NotFoundException("Invalid user id")));
        fileRepository.save(metaData);
    }

    // Download File
    public HashMap<String, Object> downloadFile(String fileName) throws IOException {
        FileMetaData file = fileRepository.findByOriginalName(fileName)
                .orElseThrow(() -> new NotFoundException("No file found with name: " + fileName));

        if (isNotAccessible(file)) throw new AccessDeniedException("you are not allowed to access this file");

        Path filePath = storage.getUploadPath().resolve(fileName).normalize();
        if (!Files.exists(filePath)) throw new FileNotFoundException("File not found: " + fileName);

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("File is not readable: " + fileName);
        }

        HashMap<String, Object> fileMap = new HashMap<>();
        fileMap.put("resource", resource);
        fileMap.put("content_type", Files.probeContentType(filePath));

        log.info("File {} downloaded by user: {}", file.getFileName(), getCurrentUser().getEmail());
        return fileMap;
    }

    // Get own Files
    public List<FileView> getFiles() {
        List<FileView> fileList = fileRepository.findAllByOwner(getCurrentUser().getUsername());
        if (fileList.isEmpty())
            throw new NotFoundException("No files found uploaded by the user: " + securityService.getCurrentUser().getUsername());
        return fileList;
    }

    // Get Files based on access level
    public List<FileView> getAccessibleFiles() {
        List<FileView> fileList = fileRepository.findAllByAccessWeightLessThanEqualAndTeam_Id(getCurrentUser().getRole().getLevel(), getCurrentUser().getTeam().getId());
        if (fileList.isEmpty())
            throw  new NotFoundException("No Files found");
        return fileList;
    }

    public void deleteFile(String fileName) throws IOException{
        FileMetaData file = fileRepository.findByOriginalName(fileName)
                .orElseThrow(() -> new NotFoundException("No File found with name: " + fileName));

        if (isNotAccessible(file)) throw new AccessDeniedException("You cannot access this file");

        Path filePath = storage.getUploadPath().resolve(fileName).normalize();
        if (!Files.exists(filePath)) throw new FileNotFoundException("File not found: " + fileName);

        fileRepository.deleteById(file.getId());
        Files.delete(filePath);
        log.info("File {} deleted by user: {}", file.getFileName(), getCurrentUser().getEmail());
    }

    private boolean isNotAccessible(FileMetaData file){
        return (file.getAccessWeight() > getCurrentUser().getRole().getLevel() ||
                Objects.equals(file.getTeam().getId(), getCurrentUser().getTeam().getId()));
    }

    private User getCurrentUser(){
        long userId = securityService.getCurrentUser().getId();
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No user found with user id: " + userId));
    }
}
