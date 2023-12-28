package com.example.springproject.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springproject.dto.UserDTO;
import com.example.springproject.dto.UserEmailUpdateDTO;
import com.example.springproject.dto.UserPasswordUpdateDTO;
import com.example.springproject.entity.User;
import com.example.springproject.service.IUserService;
import com.example.springproject.service.UserDTOMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final IUserService userService;
    private final UserDTOMapper userDTOMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(userDTOMapper.apply(user), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> response = users.stream()
                .map(userDTOMapper)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody User user) {
        user = userService.registerUser(user);
        return new ResponseEntity<>(userDTOMapper.apply(user), HttpStatus.CREATED);
    }

    @PutMapping("/update_email/{id}")
    public ResponseEntity<UserDTO> updateEmail(@Valid @RequestBody UserEmailUpdateDTO userDTO,
            @PathVariable Long id) {

        if (userService.isAuthorizedOrAdmin(id)) {
            User user = userService.updateEmail(id, userDTO);
            return new ResponseEntity<>(userDTOMapper.apply(user), HttpStatus.OK);
        } else {
            throw new AccessDeniedException("User not authorized to change email.");
        }

    }

    @PutMapping("/update_password/{id}")
    public ResponseEntity<HttpStatus> updatePassword(@Valid @RequestBody UserPasswordUpdateDTO userDTO,
            @PathVariable Long id) {

        if (userService.isAuthorizedOrAdmin(id)) {
            userService.updatePassword(id, userDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new AccessDeniedException("User not authorized to change password.");
        }
    }

}
