package users.rishik.SecureDoc.Services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Config.FileStorageProperties;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class StorageService {
    private final FileStorageProperties storage;
    private final FileDbService fileDbService;

    StorageService(FileStorageProperties storage, FileDbService fileDbService){
        this.storage = storage;
        this.fileDbService = fileDbService;
    }

    public void storeFile(MultipartFile file, Roles accessLevel, User currentUser) throws IOException {
        Roles currentUserRole = currentUser.getRole();

        if (file.isEmpty()) throw new IllegalArgumentException("Empty File submitted");
        Path targetPath = storage.getUploadPath().resolve(Objects.requireNonNull(file.getOriginalFilename())).normalize();
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        if (accessLevel == null) accessLevel = currentUserRole;
        if (currentUserRole.getLevel() >= accessLevel.getLevel())
            fileDbService.saveMetaData(file, accessLevel, currentUser);
        else throw new AccessDeniedException("You can only upload files with access level " + currentUserRole + " or lower");
    }

    public Resource getFileResource(long id, User currentUser) throws IOException {
        FileMetaData file = fileDbService.getFile(id, currentUser);
        String fileName = file.getOriginalName();

        Path filePath = getPath(fileName);
        checkFileAvailability(filePath, fileName);

        Resource resource = new UrlResource(filePath.toUri());
        checkFileReadability(resource, fileName);

        return resource;
    }

    public void deleteFile(long id, User currentUser) throws IOException {
        FileMetaData file = fileDbService.getFile(id, currentUser);
        Path filePath = getPath(file.getOriginalName());

        fileDbService.deleteFileData(id);
        Files.delete(filePath);
    }

    private Path getPath(String fileName) throws IOException {
        Path filePath = storage.getUploadPath().resolve(fileName).normalize();
        if (!Files.exists(filePath)) throw new FileNotFoundException("File not found: " + fileName);
        return filePath;
    }

    private void checkFileAvailability(Path filePath, String fileName) throws FileNotFoundException {
        if (!Files.exists(filePath)) throw new FileNotFoundException("File was moved or removed. File name: " + fileName);
    }

    private void checkFileReadability(Resource resource, String fileName) throws FileNotFoundException {
        if (!resource.isReadable()) {
            throw new FileNotFoundException("File is not readable. File name: " + fileName);
        }
    }
}
