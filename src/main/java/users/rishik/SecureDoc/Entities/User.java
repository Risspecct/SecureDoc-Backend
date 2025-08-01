package users.rishik.SecureDoc.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import users.rishik.SecureDoc.Enums.Roles;

@NoArgsConstructor
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

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    @Enumerated(EnumType.STRING)
    private Roles role;

    @ManyToOne
    private Team team;
}
