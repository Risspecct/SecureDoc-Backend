package users.rishik.SecureDoc.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import users.rishik.SecureDoc.Enums.Roles;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    private String username;

    @Column
    private Roles role;

    @Column
    private String organization;
}
