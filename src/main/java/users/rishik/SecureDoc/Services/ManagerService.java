package users.rishik.SecureDoc.Services;

import org.springframework.stereotype.Service;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Projections.UserView;
import users.rishik.SecureDoc.Repositories.FileRepository;
import users.rishik.SecureDoc.Repositories.UserRepository;
import users.rishik.SecureDoc.Security.Service.SecurityService;

import java.util.List;

@Service
public class ManagerService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    ManagerService(FileRepository fileRepository, UserRepository userRepository, SecurityService securityService){
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    public List<UserView> getTeam(){
        User user = userRepository.findById(securityService.getCurrentUser().getId())
                .orElseThrow(() -> new NotFoundException("Unable to find user details"));
        List<UserView> userList = userRepository.findAllByTeam(user.getTeam());
        if (userList.isEmpty()) throw new NotFoundException("No team members found");
        else return userList;
    }
}
