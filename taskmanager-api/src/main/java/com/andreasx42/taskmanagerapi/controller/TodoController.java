package com.andreasx42.taskmanagerapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreasx42.taskmanagerapi.dto.TodoDto;
import com.andreasx42.taskmanagerapi.exception.ErrorResponse;
import com.andreasx42.taskmanagerapi.service.impl.TodoService;
import com.andreasx42.taskmanagerapi.service.mapper.impl.TodoMapper;

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
    private final TodoMapper todoMapper;

    // GET todo by todoid
    @GetMapping(value = "/{todoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns a todo based on provided todo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Todo doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "200", description = "Successful retrieval of todo", content = @Content(schema = @Schema(implementation = TodoDto.class))),
    })
    public ResponseEntity<TodoDto> getTodoById(@PathVariable Long todoId) {

        TodoDto todoDto = todoMapper.mapFromEntity(todoService.getById(todoId));
        return new ResponseEntity<>(todoDto, HttpStatus.OK);
    }

    // GET all todos by userid
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves paged list of todos of user")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all todos of user", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TodoDto.class))))
    public ResponseEntity<Page<TodoDto>> getAllTodosOfUser(@PathVariable Long userId, Pageable pageable) {

        Page<TodoDto> todos = todoService.getAllByUserId(userId, pageable);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    // GET all todos
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves paged list of todos")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all todos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TodoDto.class))))
    public ResponseEntity<Page<TodoDto>> getAllTodos(Pageable pageable) {

        Page<TodoDto> todos = todoService.getAll(pageable);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    // CREATE todo by userid
    @PostMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Creates a todo from provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful creation of todo", content = @Content(schema = @Schema(implementation = TodoDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TodoDto> createTodo(@PathVariable Long userId, @RequestBody TodoDto todoDto) {

        todoDto = todoService.create(userId, todoDto);
        return new ResponseEntity<>(todoDto, HttpStatus.CREATED);
    }

    // UPDATE todo by userid and todoid
    @PutMapping(value = "/user/{userId}/todo/{todoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Updates a todo by user and todo IDs and provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of todo", content = @Content(schema = @Schema(implementation = TodoDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TodoDto> updateTodo(@PathVariable Long userId, @PathVariable Long todoId,
            @RequestBody TodoDto todoDto) {

        todoDto = todoService.update(todoId, todoDto);
        return new ResponseEntity<>(todoDto, HttpStatus.OK);
    }

    // DELETE todo by userid and todoid
    @DeleteMapping("/user/{userId}/todo/{todoId}")
    @PreAuthorize("#userId == principal.id or hasAuthority('ADMIN')")
    @Operation(summary = "Deletes todo with given user and todo IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful deletion of todo", content = @Content(schema = @Schema(implementation = HttpStatus.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<HttpStatus> deleteTodo(@PathVariable Long userId, @PathVariable Long todoId) {

        todoService.delete(todoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
