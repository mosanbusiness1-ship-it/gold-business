package com.mo.mappers;

import org.mapstruct.Mapper;

import com.mo.auth.User;
import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.UserDTO;


@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDTO>{
	
	public UserDTO toDto(User entity);

	public User toEntity(UserDTO dto);

    default Long toId(User user) {
        return user != null ? user.getId() : null;
    }

    default User fromId(Long id) {
        if (id == null)
            return null;
        User user = new User();
        user.setId(id);
        return user;
    }
}
