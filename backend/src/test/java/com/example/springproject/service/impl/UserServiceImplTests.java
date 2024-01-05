package com.example.springproject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.springproject.entity.User;
import com.example.springproject.exception.DuplicateEntityException;
import com.example.springproject.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	public void setUp() {
	}

	@Test
	public void testThatRegisterNewUserPasses() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setUsername("testUser");
		user.setPassword("password");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		User registeredUser = userService.create(user);

		assertEquals(registeredUser.getPassword(), "encodedPassword");

		// Verify that userRepository methods were called
		verify(userRepository, times(1)).findByEmail(anyString());
		verify(userRepository, times(1)).findByUsername(anyString());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void testThatRegisterUserWithDuplicateEmailFails() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setUsername("testUser");
		user.setPassword("password");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		assertThrows(DuplicateEntityException.class, () -> userService.create(user));

		// Verify that userRepository methods were called
		verify(userRepository, times(1)).findByEmail(anyString());
		verify(userRepository, times(0)).findByUsername(anyString());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void testThatRegisterUserWithDuplicateUsernameFails() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setUsername("testUser");
		user.setPassword("password");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

		assertThrows(DuplicateEntityException.class, () -> userService.create(user));

		// Verify that userRepository methods were called
		verify(userRepository, times(1)).findByEmail(anyString());
		verify(userRepository, times(1)).findByUsername(anyString());
		verify(userRepository, times(0)).save(user);
	}

}
