package users.rishik.SecureDoc.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String username;

    @Column
    private String role;

    @Column
    private String organization;
}
