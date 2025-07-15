package users.rishik.SecureDoc.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import users.rishik.SecureDoc.DTOs.UserRegisterDto;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Mappers.UserMapper;
import users.rishik.SecureDoc.Projections.UserView;
import users.rishik.SecureDoc.Repositories.UserRepository;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    UserService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserView addUser(UserRegisterDto userRegisterDto){
        User user = this.userMapper.toUser(userRegisterDto);
        if ( this.userRepository.existsByEmail(user.getEmail()))
            throw new IllegalArgumentException("User with this email already exists");
        this.userRepository.save(user);
        log.info("User with email: {} registered", user.getEmail());
        return this.userRepository.findUserByEmail(user.getEmail());
    }
}
