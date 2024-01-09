package com.andreasx42.taskmanagerapi.dto;

import com.andreasx42.taskmanagerapi.entity.User.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Object representing a user.")
public record UserDto(

                Long id,
                @NotNull String username,
                String email,
                Role role,
                String password) {

}