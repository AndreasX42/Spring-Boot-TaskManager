package com.andreasx42.taskmanagerapi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.andreasx42.taskmanagerapi.TestDataUtil;
import com.andreasx42.taskmanagerapi.dto.TodoDto;
import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.Todo;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.repository.TodoRepository;

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
        UserDto userDto = userService.create(TestDataUtil.getRegisteredUser());
        todoService.create(userDto.id(), TestDataUtil.getExistingTodoForRegisteredUser());
    }

    @Test
    public void assertThatCreateTodoPasses() {

        User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
        TodoDto newTodoDto = TestDataUtil.getNewTodoDto();

        long numTodosBefore = todoRepository.count();
        newTodoDto = todoService.create(user.getId(), newTodoDto);
        long numTodosAfter = todoRepository.count();

        assertEquals(1, numTodosAfter - numTodosBefore);
        assertEquals(TestDataUtil.getNewTodoDto().name(), newTodoDto.name());
        assertEquals(TestDataUtil.getNewTodoDto().priority(), newTodoDto.priority());
        assertEquals(TestDataUtil.getNewTodoDto().untilDate(), newTodoDto.untilDate());
        assertEquals(TestDataUtil.getNewTodoDto().status(), Todo.Status.OPEN);
        assertEquals(user.getId(), newTodoDto.userId());

    }

    @Test
    public void assertThatUpdateTodoPasses() {

        User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
        Todo oldTodo = todoService.getByNameAndUserId(TestDataUtil.getExistingTodoForRegisteredUser().name(),
                user.getId());

        TodoDto updatedTodoDto = TestDataUtil.getUpdatedTodoDto(oldTodo.getId(), user.getId());

        TodoDto result = todoService.update(oldTodo.getId(), updatedTodoDto);

        assertEquals(updatedTodoDto.name(), result.name());
        assertEquals(updatedTodoDto.priority(), result.priority());
        assertEquals(updatedTodoDto.status(), result.status());
        assertEquals(updatedTodoDto.untilDate(), result.untilDate());
        assertEquals(user.getId(), result.userId());
    }

    @Test
    public void assertThatDeleteTodoPasses() {

        User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
        Todo todo = todoService.getByNameAndUserId(TestDataUtil.getExistingTodoForRegisteredUser().name(),
                user.getId());

        int numUserTodosBefore = todoService.getByUserId(user.getId()).size();
        long numAllTodosBefore = todoRepository.count();
        todoService.delete(todo.getId());
        int numUserTodosAfter = todoService.getByUserId(user.getId()).size();
        long numAllTodosAfter = todoRepository.count();

        assertEquals(1, numAllTodosBefore - numAllTodosAfter);
        assertEquals(1, numUserTodosBefore - numUserTodosAfter);
    }

    @Test
    public void assertThatWithUserDeletionAlsoTodosAreDeleted() {

        User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
        List<TodoDto> userTodoDtos = todoService.getByUserId(user.getId());

        long numAllTodosBefore = todoRepository.count();
        userService.delete(user.getId());
        long numAllTodosAfter = todoRepository.count();
        int numUserTodosAfter = todoService.getByUserId(user.getId()).size();

        assertEquals(userTodoDtos.size(), numAllTodosBefore - numAllTodosAfter);
        assertEquals(0, numUserTodosAfter);

        List<Long> userTodoIds = userTodoDtos.stream().map(TodoDto::id).collect(Collectors.toList());
        for (Long todoId : userTodoIds) {
            assertTrue(todoRepository.findById(todoId).isEmpty());
        }

    }

}
