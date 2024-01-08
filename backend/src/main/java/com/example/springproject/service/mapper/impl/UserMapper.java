package com.example.springproject.service.mapper.impl;

import org.springframework.stereotype.Component;

import com.example.springproject.dto.UserDto;
import com.example.springproject.entity.User;
import com.example.springproject.service.mapper.IMapper;

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
