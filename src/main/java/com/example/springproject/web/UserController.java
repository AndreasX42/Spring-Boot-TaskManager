package com.example.springproject.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springproject.dto.TodoDTO;
import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.Todo;
import com.example.springproject.entity.User;
import com.example.springproject.service.api.IUserService;
import com.example.springproject.service.utils.UserDTOMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final IUserService userService;
    private final UserDTOMapper userDTOMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return new ResponseEntity<>(userDTOMapper.apply(user), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<UserDTO>> getAllTodos(Pageable pageable) {
        Page<User> todos = userService.getAll(pageable);
        Page<UserDTO> response = todos.map(userDTOMapper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody User user) {
        user = userService.create(user);
        return new ResponseEntity<>(userDTOMapper.apply(user), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO,
            @PathVariable Long id) {

        if (userService.isAuthorizedOrAdmin(id)) {
            User user = userService.update(id, userDTO);
            return new ResponseEntity<>(userDTOMapper.apply(user), HttpStatus.OK);
        } else {
            throw new AccessDeniedException("User not authorized to change email.");
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {

        if (userService.isAuthorizedOrAdmin(id)) {
            userService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new AccessDeniedException("User not authorized to change email.");
        }
    }
}
