package users.rishik.SecureDoc.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import users.rishik.SecureDoc.Entities.Team;
import users.rishik.SecureDoc.Projections.TeamListView;

import java.util.List;


public interface TeamRepository extends JpaRepository<Team, Long> {
    Boolean existsByName(String name);
    List<TeamListView> findAllBy();
}
