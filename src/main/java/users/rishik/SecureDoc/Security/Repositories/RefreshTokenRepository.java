package users.rishik.SecureDoc.Security.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Security.Principals.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken findByUser(User user);
}