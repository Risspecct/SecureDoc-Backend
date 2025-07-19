package users.rishik.SecureDoc.Services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Config.FileStorageProperties;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Projections.FileView;
import users.rishik.SecureDoc.Repositories.FileRepository;
import users.rishik.SecureDoc.Security.Service.SecurityService;

import java.io.FileNotFoundException;
import java.io.IOException;
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
    public void uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("Empty File submitted");
        Path targetPath = storage.getUploadPath().resolve(Objects.requireNonNull(file.getOriginalFilename())).normalize();
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        saveMetaData(file);
    }

    public void saveMetaData(MultipartFile file){
        FileMetaData metaData = new FileMetaData();
        metaData.setOriginalName(file.getOriginalFilename());
        metaData.setFileName(file.getName());
        metaData.setSize(file.getSize());
        metaData.setCreatedAt(LocalDateTime.now());
        metaData.setContentType(file.getContentType());
        metaData.setOwner(securityService.getCurrentUser().getUsername());
        metaData.setAccessLevel(securityService.getCurrentUser().getRole());
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
        List<FileView> fileList = this.fileRepository.findAllByOwner(securityService.getCurrentUser().getUsername());
        if (fileList.isEmpty())
            throw new NotFoundException("No files found uploaded by the user: " + securityService.getCurrentUser().getUsername());
        return fileList;
    }
}
