package users.rishik.SecureDoc.Services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Config.FileStorageProperties;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Repositories.FileRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FileStorageProperties storage;
    private final UserService userService;

    @Autowired
    private HttpServletRequest request;

    FileService(FileRepository fileRepository, FileStorageProperties storage, UserService userService){
        this.fileRepository = fileRepository;
        this.storage = storage;
        this.userService = userService;
    }

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
        metaData.setOwner(this.userService.getUsername());
        this.fileRepository.save(metaData);
    }

    public HashMap<String, Object> downloadFile(String fileName) throws IOException {
        Path filePath = storage.getUploadPath().resolve(fileName).normalize();
        if (!Files.exists(filePath)) throw new FileNotFoundException("Not");
        HashMap<String, Object> fileMap = new HashMap<>();
        fileMap.put("resource", new UrlResource(filePath.toUri()));
        fileMap.put("content_type", Files.probeContentType(filePath));

        return fileMap;
    }
}
