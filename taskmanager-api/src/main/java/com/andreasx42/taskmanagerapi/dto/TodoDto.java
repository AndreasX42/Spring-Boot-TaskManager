package com.andreasx42.taskmanagerapi.dto;

import java.time.LocalDate;

import com.andreasx42.taskmanagerapi.entity.Todo.Priority;
import com.andreasx42.taskmanagerapi.entity.Todo.Status;

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
