package users.rishik.SecureDoc.Mappers;

import org.mapstruct.*;
import users.rishik.SecureDoc.DTOs.UserRegisterDto;
import users.rishik.SecureDoc.DTOs.UserUpdateDto;
import users.rishik.SecureDoc.Entities.User;
import users.rishik.SecureDoc.Enums.Roles;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(UserRegisterDto userRegisterDto);

    @AfterMapping
    default void defaultRole (@MappingTarget User user){
        System.out.println("Default Role set");
        user.setRole(Roles.USER);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateDto userUpdateDto, @MappingTarget User user);
}
