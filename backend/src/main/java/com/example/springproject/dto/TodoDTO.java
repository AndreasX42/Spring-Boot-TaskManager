package com.example.springproject.dto;

import java.time.LocalDate;

import com.example.springproject.entity.Todo.Priority;
import com.example.springproject.entity.Todo.Status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

@Schema(description = "Object representing a todo.")
public record TodoDTO(

                @Nonnull String name,
                Priority priority,
                Status status,
                LocalDate untilDate) {

}
