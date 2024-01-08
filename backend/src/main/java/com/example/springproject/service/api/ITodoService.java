package com.example.springproject.service.api;

import java.util.List;

import com.example.springproject.dto.TodoDto;
import com.example.springproject.entity.Todo;

public interface ITodoService extends IService<Todo, TodoDto> {

    TodoDto create(Long id, TodoDto todo);

    Todo getByNameAndUserId(String name, Long userId);

    List<TodoDto> getByUserId(Long userId);

}
