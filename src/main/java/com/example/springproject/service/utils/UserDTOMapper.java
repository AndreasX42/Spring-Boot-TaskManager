package com.example.springproject.service.utils;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.User;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {

    @Override
    public UserDTO apply(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), null);
    }
}
