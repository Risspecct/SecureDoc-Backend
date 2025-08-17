package users.rishik.SecureDoc.Services;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Projections.FileView;
import users.rishik.SecureDoc.Repositories.FileRepository;
import users.rishik.SecureDoc.Repositories.UserRepository;
import users.rishik.SecureDoc.Security.Service.SecurityService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final FileDbService fileDbService;

    FileService(FileRepository fileRepository, SecurityService securityService, UserRepository userRepository,
                StorageService storageService, FileDbService fileDbService) {
        this.fileRepository = fileRepository;
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.fileDbService = fileDbService;
    }

    // Add File
    public void uploadFile(MultipartFile file, Roles accessLevel) throws IOException {
        storageService.storeFile(file, accessLevel, getCurrentUser());
        log.info("File {} uploaded by user: {}", file.getOriginalFilename(), getCurrentUser().getEmail());
    }

    // Download File
    public ResponseEntity<Resource> getFileResource(long id) throws IOException {
        FileMetaData file = fileRepository.findById(id).orElseThrow(() -> new NotFoundException("File not found with id: " + id));
        Resource resource = storageService.getFileResource(id, getCurrentUser());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                .body(resource);
    }

    // Get own Files
    public List<FileView> getFiles() {
        return fileDbService.getOwnFiles(getCurrentUser());
    }

    // Get Files based on access level
    public List<FileView> getAccessibleFiles() {
        return fileDbService.getFilesByAccessibility(getCurrentUser());
    }

    // Delete File by id
    public void deleteFile(long id) throws IOException{
        storageService.deleteFile(id, getCurrentUser());
        log.info("File with id:{} deleted by user: {}", id, getCurrentUser().getEmail());
    }

    private User getCurrentUser(){
        long userId = securityService.getCurrentUser().getId();
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No user found with user id: " + userId));
    }
}
