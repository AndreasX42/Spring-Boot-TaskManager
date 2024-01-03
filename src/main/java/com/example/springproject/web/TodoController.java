package com.example.springproject.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springproject.dto.TodoDTO;
import com.example.springproject.entity.Todo;
import com.example.springproject.exception.ErrorResponse;
import com.example.springproject.service.impl.TodoService;
import com.example.springproject.service.utils.TodoDTOMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/todos")
@AllArgsConstructor
@Tag(name = "Todo Controller", description = "Endpoints to create and manage todos")
public class TodoController {

    private final TodoService todoService;
    private final TodoDTOMapper todoDTOMapper;

    @GetMapping(value = "/{todoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns a todo based on provided todo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Todo doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "200", description = "Successful retrieval of todo", content = @Content(schema = @Schema(implementation = TodoDTO.class))),
    })
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long todoId) {

        Todo todo = todoService.getById(todoId);
        return new ResponseEntity<>(todoDTOMapper.apply(todo), HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves paged list of todos")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all todos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TodoDTO.class))))
    public ResponseEntity<Page<TodoDTO>> getAllTodos(Pageable pageable) {

        Page<Todo> todos = todoService.getAll(pageable);
        Page<TodoDTO> response = todos.map(todoDTOMapper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Creates a todo from provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful creation of todo", content = @Content(schema = @Schema(implementation = TodoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TodoDTO> createTodo(@PathVariable Long userId, @RequestBody Todo todo) {

        todo = todoService.create(userId, todo);
        return new ResponseEntity<>(todoDTOMapper.apply(todo), HttpStatus.CREATED);
    }

    @PutMapping(value = "/user/{userId}/todo/{todoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Updates a todo by user and todo IDs and provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of todo", content = @Content(schema = @Schema(implementation = TodoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long userId, @PathVariable Long todoId,
            @RequestBody TodoDTO todoDTO) {

        Todo todo = todoService.update(todoId, todoDTO);
        return new ResponseEntity<>(todoDTOMapper.apply(todo), HttpStatus.OK);
    }

    @DeleteMapping("/user/{userId}/todo/{todoId}")
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Deletes todo with given user and todo IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful deletion of todo", content = @Content(schema = @Schema(implementation = HttpStatus.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<HttpStatus> deleteTodo(@PathVariable Long userId, @PathVariable Long todoId) {

        todoService.delete(todoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
