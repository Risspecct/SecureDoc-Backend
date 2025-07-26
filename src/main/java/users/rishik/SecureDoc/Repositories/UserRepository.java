package users.rishik.SecureDoc.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Projections.UserProfileView;
import users.rishik.SecureDoc.Projections.UserView;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    UserView findUserByEmail(String email);
    Optional<UserProfileView> findUserById(long id);
    Optional<User> findByEmail(String email);
    List<UserView> findAllBy();

    @Query("SELECT u FROM User u WHERE u.team.id = :teamId")
    List<UserView> findAllByTeamId(Long teamId);
}
