package com.example.springproject.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.example.springproject.TestDataUtil;
import com.example.springproject.dto.TodoDTO;
import com.example.springproject.entity.Todo;
import com.example.springproject.entity.User;
import com.example.springproject.repository.TodoRepository;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TodoServiceIntegrationTests {

    private TodoService todoService;
    private TodoRepository todoRepository;
    private UserService userService;

    @Autowired
    public TodoServiceIntegrationTests(TodoService todoService, TodoRepository todoRepository,
            UserService userService) {
        this.todoService = todoService;
        this.todoRepository = todoRepository;
        this.userService = userService;
    }

    @BeforeEach
    public void setUp() {
        User user = userService.create(TestDataUtil.getRegisteredUser());
        todoService.create(user.getId(), TestDataUtil.getExistingTodoForRegisteredUser());
    }

    @Test
    public void assertThatCreateTodoPasses() {

        long numTodosBefore = todoRepository.count();
        User user = userService.getByName(TestDataUtil.getRegisteredUser().getUsername());
        Todo newTodo = TestDataUtil.getNewTodo();

        newTodo = todoService.create(user.getId(), newTodo);
        long numTodosAfter = todoRepository.count();

        assertEquals(1, numTodosAfter - numTodosBefore);
        assertEquals(TestDataUtil.getNewTodo().getName(), newTodo.getName());
        assertEquals(TestDataUtil.getNewTodo().getPriority(), newTodo.getPriority());
        assertEquals(TestDataUtil.getNewTodo().getUntilDate(), newTodo.getUntilDate());
        assertEquals(TestDataUtil.getNewTodo().getStatus(), Todo.Status.OPEN);
        assertEquals(user.getId(), newTodo.getUser().getId());
        assertNotNull(newTodo.getId());

    }

    @Test
    public void assertThatUpdateTodoPasses() {

        User user = userService.getByName(TestDataUtil.getRegisteredUser().getUsername());
        Todo oldTodo = todoService.getByNameAndUserId(TestDataUtil.getExistingTodoForRegisteredUser().getName(),
                user.getId());

        TodoDTO updatedTodoDTO = TestDataUtil.getUpdatedTodoDTO();

        Todo updatedTodo = todoService.update(oldTodo.getId(), updatedTodoDTO);

        assertEquals(TestDataUtil.getUpdatedTodoDTO().name(), updatedTodo.getName());
        assertEquals(TestDataUtil.getUpdatedTodoDTO().priority(), updatedTodo.getPriority());
        assertEquals(TestDataUtil.getUpdatedTodoDTO().status(), updatedTodo.getStatus());
        assertEquals(TestDataUtil.getUpdatedTodoDTO().untilDate(), updatedTodo.getUntilDate());
        assertEquals(user.getId(), updatedTodo.getUser().getId());
    }

}
