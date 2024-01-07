package com.example.springproject.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.springproject.dto.TodoDTO;
import com.example.springproject.entity.Todo;
import com.example.springproject.entity.User;
import com.example.springproject.exception.EntityNotFoundException;
import com.example.springproject.repository.TodoRepository;
import com.example.springproject.service.api.ITodoService;
import com.example.springproject.service.api.IUserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TodoService implements ITodoService {

    private final TodoRepository todoRepository;
    private final IUserService userService;

    public Todo getById(Long id) {
        Optional<Todo> todoOptional = todoRepository.findById(id);

        return todoOptional.orElseThrow(() -> new EntityNotFoundException(id, Todo.class));
    }

    public Page<Todo> getAll(Pageable pageable) {
        return todoRepository.findAll(pageable);
    }

    public Todo create(Long userId, Todo todo) {
        User user = userService.getById(userId);
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    public Todo update(Long id, TodoDTO todoDTO) {

        Todo todoDb = getById(id);
        todoDb.setName(todoDTO.name());
        todoDb.setPriority(todoDTO.priority());
        todoDb.setStatus(todoDTO.status());
        todoDb.setUntilDate(todoDTO.untilDate());

        return todoRepository.save(todoDb);

    }

    public void delete(Long id) {
        todoRepository.deleteById(id);
    }

    @Override
    public Todo getByNameAndUserId(String name, Long userId) {
        return todoRepository.findByNameAndUser_Id(name, userId)
                .orElseThrow(() -> new EntityNotFoundException(name, Todo.class));
    }

}
