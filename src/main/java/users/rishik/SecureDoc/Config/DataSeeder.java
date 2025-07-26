package users.rishik.SecureDoc.Config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import users.rishik.SecureDoc.Entities.Team;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Exceptions.NotFoundException;
import users.rishik.SecureDoc.Repositories.TeamRepository;
import users.rishik.SecureDoc.Repositories.UserRepository;

@SuppressWarnings("unused")
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @PostConstruct
    public void seedData(){
        createUserIfNotExists("default1@example.com", Roles.USER);
        createUserIfNotExists("default2@example.com", Roles.USER);
        createUserIfNotExists("default3@example.com", Roles.USER);
        createUserIfNotExists("default4@example.com", Roles.USER);
        createUserIfNotExists("manager1@example.com", Roles.MANAGER);
        createUserIfNotExists("manager2@example.com", Roles.MANAGER);
        createUserIfNotExists("admin@example.com", Roles.ADMIN);

        createTeamIfNotExists("Team 1", "manager1@example.com");
        createTeamIfNotExists("Team 2", "manager2@example.com");
    }

    private void createUserIfNotExists(String email, Roles role){
        if (userRepository.existsByEmail(email)) return;
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode("pas123"));
        user.setRole(role);
        user.setUsername(email);

        userRepository.save(user);
        System.out.println("User data seeded successfully");
    }

    private void createTeamIfNotExists(String name, String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found in seeder"));
        Team team = new Team();
        team.setName(name);
        team.setLead(user);
        team.getMembers().add(user);
        teamRepository.save(team);

        user.setTeam(team);
        userRepository.save(user);

        System.out.println("Team data seeded successfully");
    }
}
