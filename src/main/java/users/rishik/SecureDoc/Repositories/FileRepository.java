package users.rishik.SecureDoc.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Projections.FileView;

import java.util.List;

public interface FileRepository extends JpaRepository<FileMetaData, Long> {
    List<FileView> findAllByOwner(String owner);
}
