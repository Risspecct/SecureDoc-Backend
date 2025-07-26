package users.rishik.SecureDoc.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import users.rishik.SecureDoc.Entities.FileMetaData;
import users.rishik.SecureDoc.Projections.FileView;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileMetaData, Long> {
    Optional<FileMetaData> findByOriginalName(String originalName);

    List<FileView> findAllByOwner(String owner);
    List<FileView> findAllByAccessWeightLessThanEqualAndTeam_Id(int accessWeight, Long teamId);
}
