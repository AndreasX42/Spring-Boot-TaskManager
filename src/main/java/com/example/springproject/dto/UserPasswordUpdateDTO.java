package com.example.springproject.dto;

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
    @NotBlank(message = "password cannot be blank")
    private String password;

}
