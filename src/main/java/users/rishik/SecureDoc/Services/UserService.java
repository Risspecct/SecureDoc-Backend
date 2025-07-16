package users.rishik.SecureDoc.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import users.rishik.SecureDoc.DTOs.UserRegisterDto;
import users.rishik.SecureDoc.DTOs.UserUpdateDto;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Mappers.UserMapper;
import users.rishik.SecureDoc.Projections.UserProfileView;
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
}
