package users.rishik.SecureDoc.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Team {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private long id;

    @Column( unique = true )
    private String name;

    @OneToOne
    @JoinColumn( name = "leader_id", referencedColumnName = "id")
    private User lead;

    @OneToMany(mappedBy = "team")
    private Set<User> members = new HashSet<>();
}
