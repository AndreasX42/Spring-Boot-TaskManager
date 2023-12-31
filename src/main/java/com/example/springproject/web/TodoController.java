package com.example.springproject.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springproject.dto.TodoDTO;
import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.Todo;
import com.example.springproject.service.impl.TodoService;
import com.example.springproject.service.utils.TodoDTOMapper;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/todos")
@AllArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final TodoDTOMapper todoDTOMapper;

    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id) {
        Todo todo = todoService.getById(id);
        return new ResponseEntity<>(todoDTOMapper.apply(todo), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<TodoDTO>> getAllTodos(Pageable pageable) {
        Page<Todo> todos = todoService.getAll(pageable);
        Page<TodoDTO> response = todos.map(todoDTOMapper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create/user/{userId}")
    public ResponseEntity<TodoDTO> createTodo(@PathVariable Long userId, @RequestBody Todo todo) {
        todo = todoService.create(userId, todo);
        return new ResponseEntity<>(todoDTOMapper.apply(todo), HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id, @RequestBody TodoDTO todoDTO) {
        Todo todo = todoService.update(id, todoDTO);
        return new ResponseEntity<>(todoDTOMapper.apply(todo), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTodo(@PathVariable Long id) {
        todoService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
