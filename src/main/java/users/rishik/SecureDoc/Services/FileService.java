package users.rishik.SecureDoc.Services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Config.FileStorageProperties;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Projections.FileView;
import users.rishik.SecureDoc.Repositories.FileRepository;
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

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FileStorageProperties storage;
    private final SecurityService securityService;

    FileService(FileRepository fileRepository, FileStorageProperties storage, SecurityService securityService) {
        this.fileRepository = fileRepository;
        this.storage = storage;
        this.securityService = securityService;
    }

    // Add File
    public void uploadFile(MultipartFile file, Roles accessLevel) throws IOException {

        Roles currentUserRole = securityService.getCurrentUser().getRole();

        if (file.isEmpty()) throw new IllegalArgumentException("Empty File submitted");
        Path targetPath = storage.getUploadPath().resolve(Objects.requireNonNull(file.getOriginalFilename())).normalize();
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        if (accessLevel == null) accessLevel = currentUserRole;
        if (currentUserRole.getLevel() >= accessLevel.getLevel())
            saveMetaData(file, accessLevel);
        else throw new AccessDeniedException("You can only upload files with access level " + currentUserRole + " or lower");
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
        fileRepository.save(metaData);
    }

    // Download File
    public HashMap<String, Object> downloadFile(String fileName) throws IOException {
        Path filePath = storage.getUploadPath().resolve(fileName).normalize();
        if (!Files.exists(filePath)) throw new FileNotFoundException("File not found: " + fileName);

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("File is not readable: " + fileName);
        }

        HashMap<String, Object> fileMap = new HashMap<>();
        fileMap.put("resource", resource);
        fileMap.put("content_type", Files.probeContentType(filePath));

        return fileMap;
    }

    // Get own Files
    public List<FileView> getFiles() {
        List<FileView> fileList = fileRepository.findAllByOwner(securityService.getCurrentUser().getUsername());
        if (fileList.isEmpty())
            throw new NotFoundException("No files found uploaded by the user: " + securityService.getCurrentUser().getUsername());
        return fileList;
    }

    // Get Files based on access level
    public List<FileView> getAccessibleFiles() {
        List<FileView> fileList = fileRepository.findAllByAccessWeightLessThanEqual(securityService.getCurrentUser().getRole().getLevel());
        if (fileList.isEmpty())
            throw  new NotFoundException("No Files found");
        return fileList;
    }
}
