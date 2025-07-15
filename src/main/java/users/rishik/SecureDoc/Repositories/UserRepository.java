package users.rishik.SecureDoc.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Projections.UserView;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Boolean existsByEmail(String email);
    public UserView findUserByEmail(String email);
}
