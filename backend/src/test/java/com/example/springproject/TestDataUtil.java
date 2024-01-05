package com.example.springproject;

import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.User;

public final class TestDataUtil {

    private TestDataUtil() {
    }

    public static User registeredUser() {
        return new User("Jane Doe", "Jane.doe@gmail.com", "pw123");
    }

    public static User newUser() {
        return new User("John Doe", "john.doe@gmail.com", "pw123");
    }

    public static UserDTO updatedRegisteredUser() {
        return new UserDTO(registeredUser().getId(), registeredUser().getUsername(), "new_address@gmail.com",
                registeredUser().getRole(),
                "new_password");
    }
}
