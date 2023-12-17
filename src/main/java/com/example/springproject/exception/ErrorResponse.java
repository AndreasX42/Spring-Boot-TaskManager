package com.example.springproject.exception;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public record ErrorResponse(

        @NotNull String path,
        @NotNull String[] messages,
        @NotNull int statusCode,
        @NotNull LocalDateTime localDateTime) {

}