package com.example.springproject.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.springproject.dto.TodoDto;
import com.example.springproject.entity.Todo;
import com.example.springproject.entity.User;
import com.example.springproject.exception.EntityNotFoundException;
import com.example.springproject.repository.TodoRepository;
import com.example.springproject.service.api.ITodoService;
import com.example.springproject.service.api.IUserService;
import com.example.springproject.service.mappers.impl.TodoMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TodoService implements ITodoService {

    private final TodoRepository todoRepository;
    private final IUserService userService;
    private final TodoMapper todoMapper;

    public Todo getById(Long id) {
        Optional<Todo> todoOptional = todoRepository.findById(id);
        return todoOptional.orElseThrow(() -> new EntityNotFoundException(id, Todo.class));
    }

    public Page<TodoDto> getAll(Pageable pageable) {
        return todoRepository.findAll(pageable).map(todoMapper::mapFromEntity);
    }

    public TodoDto create(Long userId, TodoDto todoDto) {
        User user = userService.getById(userId);
        Todo todo = todoMapper.mapToEntity(todoDto);

        todo.setUser(user);
        return todoMapper.mapFromEntity(todoRepository.save(todo));
    }

    public TodoDto update(Long id, TodoDto todoDto) {

        Todo todoDb = getById(id);
        todoDb.setName(todoDto.name());
        todoDb.setPriority(todoDto.priority());
        todoDb.setStatus(todoDto.status());
        todoDb.setUntilDate(todoDto.untilDate());

        return todoMapper.mapFromEntity(todoRepository.save(todoDb));

    }

    public void delete(Long id) {
        todoRepository.deleteById(id);
    }

    @Override
    public Todo getByNameAndUserId(String name, Long userId) {
        return todoRepository.findByNameAndUser_Id(name, userId)
                .orElseThrow(() -> new EntityNotFoundException(name, Todo.class));
    }

    @Override
    public List<TodoDto> getByUserId(Long userId) {
        return todoRepository.findByUser_Id(userId).stream().map(todoMapper::mapFromEntity)
                .collect(Collectors.toList());
    }

}
