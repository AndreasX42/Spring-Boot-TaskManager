package com.example.springproject.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.User;
import com.example.springproject.exception.DuplicateEntityException;
import com.example.springproject.exception.EntityNotFoundException;
import com.example.springproject.repository.UserRepository;
import com.example.springproject.service.api.IUserService;

import lombok.AllArgsConstructor;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User getByName(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        return userOptional.orElseThrow(() -> new EntityNotFoundException(username, User.class));
    }

    @Override
    public User getById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        return userOptional.orElseThrow(() -> new EntityNotFoundException(id, User.class));
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User create(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEntityException("email", user.getEmail(), User.class);
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateEntityException("username", user.getUsername(), User.class);
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User update(Long id, UserDTO userDTO) {
        User user = getById(id);

        if (!userDTO.email().equals(user.getEmail()) && userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new DuplicateEntityException("email", userDTO.email(), User.class);
        }

        user.setEmail(userDTO.email());

        if (userDTO.password() != null
                && !bCryptPasswordEncoder.encode(userDTO.password()).equals(user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(userDTO.password()));
        }

        return userRepository.save(user);

    }

}
