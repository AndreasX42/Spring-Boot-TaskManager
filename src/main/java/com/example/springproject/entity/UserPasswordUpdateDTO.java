package com.example.springproject.entity;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class UserPasswordUpdateDTO {

    @Nonnull
    @NotBlank(message = "username cannot be blank")
    private String username;

    @Nonnull
    @NotBlank(message = "password cannot be blank")
    private String password;

}
