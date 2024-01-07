package com.example.springproject;

import java.time.LocalDate;

import com.example.springproject.dto.TodoDTO;
import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.Todo;
import com.example.springproject.entity.User;

public final class TestDataUtil {

    private final static User registeredUser = new User("Jane Doe", "Jane.doe@gmail.com", "pw123");

    private final static User newUser = new User("John Doe", "john.doe@gmail.com", "pw123");

    private final static UserDTO updatedRegisteredUserDTO = new UserDTO(getRegisteredUser().getUsername(),
            "new_address@gmail.com",
            getRegisteredUser().getRole(),
            "new_password");

    private final static Todo newTodo = new Todo("my_todo", Todo.Priority.LOW, LocalDate.of(2024, 04, 29));

    private final static TodoDTO updatedTodoDTO = new TodoDTO("my_updated_todo", Todo.Priority.HIGH,
            Todo.Status.FINISHED, LocalDate.of(2024, 03, 13));

    private final static Todo existingTodoForRegisteredUser = new Todo("my_old_todo", Todo.Priority.MID,
            LocalDate.of(2024, 03, 9));

    private TestDataUtil() {
    }

    public static User getRegisteredUser() {
        return registeredUser;
    }

    public static User getNewUser() {
        return newUser;
    }

    public static UserDTO getUpdatedRegisteredUserDTO() {
        return updatedRegisteredUserDTO;
    }

    public static Todo getNewTodo() {
        return newTodo;
    }

    public static TodoDTO getUpdatedTodoDTO() {
        return updatedTodoDTO;

    }

    public static Todo getExistingTodoForRegisteredUser() {
        return existingTodoForRegisteredUser;
    }
}
