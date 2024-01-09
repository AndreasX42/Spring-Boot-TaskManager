package com.andreasx42.taskmanagerapi.service.mapper.impl;

import org.springframework.stereotype.Component;

import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.service.mapper.IMapper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserMapper implements IMapper<User, UserDto> {

    @Override
    public UserDto mapFromEntity(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), null);
    }

    @Override
    public User mapToEntity(UserDto userDto) {
        return new User(userDto.username(), userDto.email(), userDto.password());
    }

}
