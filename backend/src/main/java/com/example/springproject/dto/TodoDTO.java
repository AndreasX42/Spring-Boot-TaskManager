package com.example.springproject.dto;

import java.time.LocalDate;

import com.example.springproject.entity.Todo.Priority;
import com.example.springproject.entity.Todo.Status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

@Schema(description = "Object representing a todo.")
public record TodoDto(

        Long id,
        Long userId,
        @Nonnull String name,
        @Nonnull Priority priority,
        @Nonnull Status status,
        @Nonnull LocalDate untilDate) {

}
