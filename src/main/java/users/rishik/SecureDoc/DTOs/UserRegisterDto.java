package users.rishik.SecureDoc.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import users.rishik.SecureDoc.Annotations.NullOrNotBlank;
import users.rishik.SecureDoc.Entities.Team;

@Data
@NoArgsConstructor
public class UserRegisterDto {
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String username;
}
