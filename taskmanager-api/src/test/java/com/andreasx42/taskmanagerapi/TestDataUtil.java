package com.andreasx42.taskmanagerapi;

import java.time.LocalDate;

import com.andreasx42.taskmanagerapi.dto.TodoDto;
import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.Todo;
import com.andreasx42.taskmanagerapi.entity.User;

public final class TestDataUtil {

    private final static UserDto registeredUser = new UserDto(null, "Jane Doe", "Jane.doe@gmail.com", User.Role.USER,
            "pw123");

    private final static UserDto newUserDto = new UserDto(null, "John Doe", "john.doe@gmail.com", User.Role.USER,
            "pw123");

    private final static TodoDto newTodoDto = new TodoDto(null, null, "my_todo", Todo.Priority.LOW, Todo.Status.OPEN,
            LocalDate.of(2024, 04, 29));

    private final static TodoDto existingTodoForRegisteredUser = new TodoDto(null, null, "my_old_todo",
            Todo.Priority.MID,
            Todo.Status.OPEN, LocalDate.of(2024, 03, 9));

    private TestDataUtil() {
    }

    public static UserDto getRegisteredUser() {
        return registeredUser;
    }

    public static UserDto getNewUserDto() {
        return newUserDto;
    }

    public static UserDto getUpdatedRegisteredUserDto(Long id) {
        return new UserDto(id, getRegisteredUser().username(),
                "new_address@gmail.com",
                getRegisteredUser().role(),
                "new_password");
    }

    public static TodoDto getNewTodoDto() {
        return newTodoDto;
    }

    public static TodoDto getUpdatedTodoDto(Long todoId, Long userId) {
        return new TodoDto(todoId, userId, "my_updated_todo", Todo.Priority.HIGH,
                Todo.Status.FINISHED, LocalDate.of(2024, 03, 13));

    }

    public static TodoDto getExistingTodoForRegisteredUser() {
        return existingTodoForRegisteredUser;
    }
}
