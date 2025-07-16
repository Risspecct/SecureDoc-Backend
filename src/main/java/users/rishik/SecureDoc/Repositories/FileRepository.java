package users.rishik.SecureDoc.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import users.rishik.SecureDoc.Entities.FileMetaData;

public interface FileRepository extends JpaRepository<FileMetaData, Long> {
}
