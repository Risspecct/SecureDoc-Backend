package users.rishik.SecureDoc.Services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Config.FileStorageProperties;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Repositories.FileRepository;

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

    FileService(FileRepository fileRepository, FileStorageProperties storage){
        this.fileRepository = fileRepository;
        this.storage = storage;
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

        this.fileRepository.save(metaData);
    }
}
