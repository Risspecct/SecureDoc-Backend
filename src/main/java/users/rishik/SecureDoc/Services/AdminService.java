package users.rishik.SecureDoc.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Projections.UserProfileView;
import users.rishik.SecureDoc.Projections.UserView;
import users.rishik.SecureDoc.Repositories.UserRepository;

import java.util.List;

@Slf4j
@Service
public class AdminService {
    private final UserRepository userRepository;

    AdminService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserProfileView getUser(long userId){
        return userRepository.findUserById(userId).orElseThrow(() -> new NotFoundException("No user exists with id: " + userId));
    }

    public List<UserView> getAllUsers(){
        List<UserView> userList = userRepository.findAllBy();
        if (userList.isEmpty()) throw new NotFoundException("No users found");
        return userList;
    }

    public void changeRole(long userId, Roles role){
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No user exists with id: " + userId));
        user.setRole(role);
        userRepository.save(user);
        log.info("Role changed for user with id:{}", user);
    }

    public void deleteUser(long userId){
        if (!userRepository.existsById(userId)) throw new NotFoundException("No user exists with id: " + userId);
        userRepository.deleteById(userId);
        log.info("User with id {} deleted", userId);
    }
}
