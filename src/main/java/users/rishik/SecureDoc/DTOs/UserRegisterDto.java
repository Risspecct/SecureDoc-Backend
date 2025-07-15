package users.rishik.SecureDoc.DTOs;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegisterDto {
    private String email;
    private String password;
    private String username;
    private String Role;
    private String Organization;
}
