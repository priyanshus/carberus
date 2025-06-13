package com.cb.carberus.user.mapper;

import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.UserDTO;
import com.cb.carberus.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );
    UserDTO toUserDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toUser(AddUserDTO userDTO);
}
