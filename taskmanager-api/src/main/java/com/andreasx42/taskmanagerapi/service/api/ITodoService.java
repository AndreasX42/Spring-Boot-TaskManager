package com.andreasx42.taskmanagerapi.service.api;

import java.util.List;

import com.andreasx42.taskmanagerapi.dto.TodoDto;
import com.andreasx42.taskmanagerapi.entity.Todo;

public interface ITodoService extends IService<Todo, TodoDto> {

    TodoDto create(Long id, TodoDto todo);

    Todo getByNameAndUserId(String name, Long userId);

    List<TodoDto> getByUserId(Long userId);

}
