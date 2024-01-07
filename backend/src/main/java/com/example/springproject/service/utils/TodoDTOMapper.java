package com.example.springproject.service.utils;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.example.springproject.dto.TodoDTO;
import com.example.springproject.entity.Todo;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TodoDTOMapper implements Function<Todo, TodoDTO> {

    @Override
    public TodoDTO apply(Todo todo) {
        return new TodoDTO(todo.getName(), todo.getPriority(), todo.getStatus(), todo.getUntilDate());
    }
}
