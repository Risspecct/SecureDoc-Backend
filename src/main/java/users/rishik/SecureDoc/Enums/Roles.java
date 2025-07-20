package users.rishik.SecureDoc.Enums;

import lombok.Getter;

@Getter
public enum Roles {
    USER(1),
    MANAGER(2),
    ADMIN(3);

    private final int level;

    Roles(int level){
        this.level = level;
    }
}
