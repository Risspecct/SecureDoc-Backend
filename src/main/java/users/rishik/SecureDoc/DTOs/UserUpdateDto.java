package users.rishik.SecureDoc.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserUpdateDto {
    private String username;
    private String organization;
}
