package users.rishik.SecureDoc.Services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import users.rishik.SecureDoc.DTOs.JwtResponseDto;
import users.rishik.SecureDoc.DTOs.LoginDto;
import users.rishik.SecureDoc.DTOs.UserRegisterDto;
import users.rishik.SecureDoc.DTOs.UserUpdateDto;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Mappers.UserMapper;
import users.rishik.SecureDoc.Projections.UserProfileView;
import users.rishik.SecureDoc.Projections.UserView;
import users.rishik.SecureDoc.Repositories.UserRepository;
import users.rishik.SecureDoc.Security.Principals.RefreshToken;
import users.rishik.SecureDoc.Security.Service.JwtService;
import users.rishik.SecureDoc.Security.Service.RefreshTokenService;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    private HttpServletRequest request;

    UserService(UserRepository userRepository, UserMapper userMapper, AuthenticationManager authenticationManager,
                JwtService jwtService, RefreshTokenService refreshTokenService){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public UserView addUser(UserRegisterDto dto){
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Email already exists");
        dto.setPassword(encoder.encode(dto.getPassword()));
        User user = this.userMapper.toUser(dto);
        this.userRepository.save(user);

        log.info("Creating a user with username: {}", user.getEmail());
        return this.userRepository.findUserByEmail(user.getEmail());
    }

    public UserProfileView getUser(long id){
        return this.userRepository.findUserById(id).orElseThrow(() -> new NotFoundException("No user associated with id: " + id));
    }

    public UserProfileView updateUser(long id, UserUpdateDto userUpdateDto){
        User user = this.userRepository.findById(id).orElseThrow(() -> new NotFoundException("No user associated with the id: " + id));
        this.userMapper.updateUserFromDto(userUpdateDto, user);
        this.userRepository.save(user);
        return getUser(user.getId());
    }

    public void deleteUser(long id){
        this.userRepository.deleteById(id);
    }

    public int getUserId() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            return jwtService.extractUserId(jwt);
        }
        throw new RuntimeException("Authorization header missing or invalid");
    }

    public JwtResponseDto verify(LoginDto user){
        if (!this.userRepository.existsByEmail(user.getEmail()))
            throw new NotFoundException("User email not found. Register to make a new account");
        Authentication auth = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        log.debug("Authenticating user info");
        if (auth.isAuthenticated()){
            RefreshToken refreshToken = this.refreshTokenService.createRefreshToken(user.getEmail());
            return JwtResponseDto.builder()
                    .accessToken(this.jwtService.generateToken(this.userRepository.findByEmail(user.getEmail())
                            .orElseThrow(() -> new UsernameNotFoundException("No user found with the provided email"))))
                    .token(refreshToken.getToken())
                    .build();
        } else {
            throw new UsernameNotFoundException("Invalid username/email provided");
        }
    }
}
