package com.example.springproject.dto;

import com.example.springproject.entity.User.Role;

import jakarta.validation.constraints.NotNull;

public record UserDTO(

        Long id,
        @NotNull String username,
        @NotNull String email,
        Role role,
        String password) {

}