package users.rishik.SecureDoc.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Projections.FileView;
import users.rishik.SecureDoc.Repositories.FileRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class FileDbService {
    private final FileRepository fileRepository;

    FileDbService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void saveMetaData(MultipartFile file, Roles accessLevel, User currentUser){
        FileMetaData metaData = new FileMetaData();

        metaData.setOriginalName(file.getOriginalFilename());
        metaData.setFileName(file.getName());
        metaData.setSize(file.getSize());
        metaData.setCreatedAt(LocalDateTime.now());
        metaData.setContentType(file.getContentType());
        metaData.setOwner(currentUser.getUsername());
        metaData.setAccessLevel(accessLevel);
        metaData.setAccessWeight(accessLevel.getLevel());
        metaData.setTeam(currentUser.getTeam());
        metaData.setUser(currentUser);
        fileRepository.save(metaData);
    }

    public FileMetaData getFile(long id, User currentUser) throws AccessDeniedException {
        FileMetaData file = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No file found with id: " + id));

        if (isNotAccessible(file, currentUser))
            throw new AccessDeniedException("You are not allowed to access this file");

        return file;
    }

    public List<FileView> getOwnFiles(User currentUser) {
        List<FileView> fileList = fileRepository.findAllByOwner(currentUser.getUsername());
        if (fileList.isEmpty())
            throw new NotFoundException("No files found uploaded by the user: " + currentUser.getUsername());
        return fileList;
    }

    public List<FileView> getFilesByAccessibility(User currentUser){
        List<FileView> fileList = fileRepository.findAllByAccessWeightLessThanEqualAndTeam_Id(currentUser.getRole().getLevel(), currentUser.getTeam().getId());
        if (fileList.isEmpty())
            throw  new NotFoundException("No Files found");
        return fileList;
    }

    public void deleteFileData(long id) {
        fileRepository.deleteById(id);
    }

    private boolean isNotAccessible(FileMetaData file, User currentUser){
        return (file.getAccessWeight() > currentUser.getRole().getLevel() ||
                !Objects.equals(file.getTeam().getId(), currentUser.getTeam().getId()));
    }
}
