package users.rishik.SecureDoc.Services;

import lombok.extern.slf4j.Slf4j;
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
import users.rishik.SecureDoc.Security.Service.SecurityService;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityService securityService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    UserService(UserRepository userRepository, UserMapper userMapper, AuthenticationManager authenticationManager,
                JwtService jwtService, RefreshTokenService refreshTokenService, SecurityService securityService){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.securityService = securityService;
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

    public UserProfileView getCurrentUser(){
        return this.userRepository.findUserById(securityService.getCurrentUser().getId())
                .orElseThrow(() -> new NotFoundException("No user associated with id: " + securityService.getCurrentUser().getId()));
    }

    public UserProfileView updateUser(UserUpdateDto userUpdateDto){
        User user = this.userRepository.findById(securityService.getCurrentUser().getId())
                .orElseThrow(() -> new NotFoundException("No user associated with the id: " + securityService.getCurrentUser().getId()));
        this.userMapper.updateUserFromDto(userUpdateDto, user);
        this.userRepository.save(user);
        return getCurrentUser();
    }

    public void deleteCurrentUser(){
        this.userRepository.deleteById(securityService.getCurrentUser().getId());
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
