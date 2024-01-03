package com.example.springproject.dto;

import java.time.LocalDate;

import com.example.springproject.entity.Todo.Priority;
import com.example.springproject.entity.Todo.Status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Object representing a todo.")
public record TodoDTO(

        Long id,
        @NotNull String name,
        @NotNull Priority priority,
        @NotNull Status status,
        @NotNull LocalDate untilDate,
        @NotNull UserDTO userDTO) {

}
