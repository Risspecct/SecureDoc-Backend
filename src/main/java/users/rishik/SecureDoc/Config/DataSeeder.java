package users.rishik.SecureDoc.Config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Repositories.UserRepository;

@SuppressWarnings("unused")
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @PostConstruct
    public void seedData(){
        createIfNotExists("default@example.com", Roles.USER);
        createIfNotExists("default1@example.com", Roles.USER);
        createIfNotExists("default2@example.com", Roles.USER);
        createIfNotExists("manager@example.com", Roles.MANAGER);
        createIfNotExists("admin@example.com", Roles.ADMIN);
    }

    private void createIfNotExists(String email, Roles role){
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode("pas123"));
        user.setRole(role);
        user.setUsername(role.toString());
        user.setTeam("Testers");

        userRepository.save(user);
        System.out.println("User data seeded successfully");
    }
}
