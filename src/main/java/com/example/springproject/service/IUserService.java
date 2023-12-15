package com.example.springproject.service;

import java.util.List;

import com.example.springproject.entity.User;
import com.example.springproject.entity.UserEmailUpdateDTO;
import com.example.springproject.entity.UserPasswordUpdateDTO;

public interface IUserService {
    User getUserById(Long id);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    User registerUser(User user);

    void updateEmail(Long id, UserEmailUpdateDTO userDTO);

    void updatePassword(Long id, UserPasswordUpdateDTO userDTO);

}
