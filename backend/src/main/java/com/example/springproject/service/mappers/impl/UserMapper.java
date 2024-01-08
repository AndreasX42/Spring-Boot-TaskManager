package com.example.springproject.service.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.springproject.dto.UserDto;
import com.example.springproject.entity.User;
import com.example.springproject.service.mappers.IMapper;

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
