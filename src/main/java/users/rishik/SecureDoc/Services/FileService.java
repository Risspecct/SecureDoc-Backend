package users.rishik.SecureDoc.Services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Config.FileStorageProperties;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Repositories.FileRepository;
import users.rishik.SecureDoc.Security.Service.JwtService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FileStorageProperties storage;
    private final JwtService jwtService;

    @Autowired
    private HttpServletRequest request;

    FileService(FileRepository fileRepository, FileStorageProperties storage, JwtService jwtService){
        this.fileRepository = fileRepository;
        this.storage = storage;
        this.jwtService = jwtService;
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
        metaData.setOwner(this.getUsername());

        this.fileRepository.save(metaData);
    }

    private String getUsername() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            return jwtService.extractUsername(jwt);
        }
        throw new RuntimeException("Authorization header missing or invalid");
    }
}
