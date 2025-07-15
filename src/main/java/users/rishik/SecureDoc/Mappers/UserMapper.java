package users.rishik.SecureDoc.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import users.rishik.SecureDoc.DTOs.UserRegisterDto;
import users.rishik.SecureDoc.Entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(UserRegisterDto userRegisterDto);
}
