package users.rishik.SecureDoc.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;
import users.rishik.SecureDoc.Annotations.NullOrNotBlank;

@NoArgsConstructor
@Data
public class UserUpdateDto {
    @NullOrNotBlank
    private String username;
    @NullOrNotBlank
    private String team;
}
