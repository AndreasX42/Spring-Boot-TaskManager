package com.example.springproject.dto;

import com.example.springproject.entity.User;
import com.example.springproject.entity.User.Role;

import jakarta.validation.constraints.NotNull;

public record UserDTO(

        @NotNull Long id,
        @NotNull String username,
        @NotNull String email,
        @NotNull Role role) {

}
