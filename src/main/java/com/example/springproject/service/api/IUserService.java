package com.example.springproject.service.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.User;

public interface IUserService extends IService<User, UserDTO> {

    User create(User user);

    User getByName(String username);

    default boolean isAuthorizedOrAdmin(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication information is not available");
        }

        String currentPrincipalName = authentication.getName();
        User user = getByName(currentPrincipalName);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> User.Role.ADMIN.toString().equals(grantedAuthority.getAuthority()));

        return isAdmin || user.getId().equals(id);
    }
}
