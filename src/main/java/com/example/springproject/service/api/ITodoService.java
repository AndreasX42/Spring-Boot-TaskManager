package com.example.springproject.service.api;

import com.example.springproject.dto.TodoDTO;
import com.example.springproject.entity.Todo;

public interface ITodoService extends IService<Todo, TodoDTO> {

    Todo create(Long id, Todo todo);

}
