package users.rishik.SecureDoc.Security.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import users.rishik.SecureDoc.Repositories.UserRepository;
import users.rishik.SecureDoc.Security.Principals.RefreshToken;
import users.rishik.SecureDoc.Security.Repositories.RefreshTokenRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository){
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String username){
        log.info("Generating new Refresh Token");
        RefreshToken existingToken = this.refreshTokenRepository.findByUser(this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with the provided email")));
        if (existingToken != null) this.refreshTokenRepository.delete(existingToken);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(this.userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("No user found with the provided email")))
                .token(UUID.randomUUID().toString())
                .expiryTime(Instant.now().plusMillis(36000000))
                .build();
        return this.refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken checkExpiry(RefreshToken token){
        log.debug("Checking validity of the Refresh Token");
        if (token.getExpiryTime().isBefore(Instant.now()))
            throw new RuntimeException(token.getToken() + " Refresh Token has expired. Please make new login");
        return token;
    }

    public Optional<RefreshToken> findByToken(String token){
        return this.refreshTokenRepository.findByToken(token);
    }
}
