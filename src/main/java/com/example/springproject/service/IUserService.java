package com.example.springproject.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import com.example.springproject.dto.UserDTO;
import com.example.springproject.dto.UserEmailUpdateDTO;
import com.example.springproject.dto.UserPasswordUpdateDTO;
import com.example.springproject.entity.User;

public interface IUserService {
    User getUserById(Long id);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    User registerUser(User user);

    User updateEmail(Long id, UserEmailUpdateDTO userDTO);

    void updatePassword(Long id, UserPasswordUpdateDTO userDTO);

    default boolean isAuthorizedOrAdmin(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication information is not available");
        }

        String currentPrincipalName = authentication.getName();
        User user = getUserByUsername(currentPrincipalName);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> User.Role.ADMIN.toString().equals(grantedAuthority.getAuthority()));

        return isAdmin || user.getId().equals(id);
    }
}
