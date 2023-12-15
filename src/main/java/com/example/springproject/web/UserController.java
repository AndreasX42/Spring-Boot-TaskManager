package com.example.springproject.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springproject.entity.User;
import com.example.springproject.entity.UserEmailUpdateDTO;
import com.example.springproject.entity.UserPasswordUpdateDTO;
import com.example.springproject.service.IUserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> registerUser(@Valid @RequestBody User user) {
        userService.registerUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update_email/{id}")
    public ResponseEntity<HttpStatus> updateEmail(@Valid @RequestBody UserEmailUpdateDTO userDTO,
            @PathVariable Long id) {
        userService.updateEmail(id, userDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update_password/{id}")
    public ResponseEntity<HttpStatus> updatePassword(@Valid @RequestBody UserPasswordUpdateDTO userDTO,
            @PathVariable Long id) {
        userService.updatePassword(id, userDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
