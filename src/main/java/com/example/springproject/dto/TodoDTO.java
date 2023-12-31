package com.example.springproject.dto;

import java.time.LocalDate;

import com.example.springproject.entity.Todo.Priority;
import com.example.springproject.entity.Todo.Status;

import jakarta.validation.constraints.NotNull;

public record TodoDTO(

                Long id,
                @NotNull String name,
                @NotNull Priority priority,
                @NotNull Status status,
                @NotNull LocalDate untilDate,
                @NotNull UserDTO userDTO) {

}
