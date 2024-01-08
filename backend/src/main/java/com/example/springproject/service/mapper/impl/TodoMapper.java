package com.example.springproject.service.mapper.impl;

import org.springframework.stereotype.Component;
import com.example.springproject.dto.TodoDto;
import com.example.springproject.entity.Todo;
import com.example.springproject.service.mapper.IMapper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TodoMapper implements IMapper<Todo, TodoDto> {

    @Override
    public TodoDto mapFromEntity(Todo todo) {
        return new TodoDto(
                todo.getId(),
                todo.getUser().getId(),
                todo.getName(),
                todo.getPriority(),
                todo.getStatus(),
                todo.getUntilDate());
    }

    @Override
    public Todo mapToEntity(TodoDto todoDto) {
        return new Todo(todoDto.name(), todoDto.priority(), todoDto.status(), todoDto.untilDate());
    }
}
