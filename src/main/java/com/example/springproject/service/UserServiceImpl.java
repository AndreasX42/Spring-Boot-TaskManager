package com.example.springproject.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springproject.dto.UserEmailUpdateDTO;
import com.example.springproject.dto.UserPasswordUpdateDTO;
import com.example.springproject.entity.User;
import com.example.springproject.exception.DuplicateEntityException;
import com.example.springproject.exception.EntityNotFoundException;
import com.example.springproject.repository.UserRepository;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent())
            return user.get();
        else
            throw new EntityNotFoundException(username, User.class);
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent())
            return user.get();
        else
            throw new EntityNotFoundException(id, User.class);
    }

    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User registerUser(User user) {

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
    public User updateEmail(Long id, UserEmailUpdateDTO userDTO) {

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new DuplicateEntityException("email", userDTO.getEmail(), User.class);
        }

        User user = getUserById(id);
        user.setEmail(userDTO.getEmail());

        return userRepository.save(user);
    }

    @Override
    public void updatePassword(Long id, UserPasswordUpdateDTO userDTO) {
        User user = getUserById(id);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
    }
}
