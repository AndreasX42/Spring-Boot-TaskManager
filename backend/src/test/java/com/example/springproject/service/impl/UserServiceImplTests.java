package com.example.springproject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.springproject.TestDataUtil;
import com.example.springproject.dto.UserDto;
import com.example.springproject.entity.User;
import com.example.springproject.exception.DuplicateEntityException;
import com.example.springproject.repository.UserRepository;
import com.example.springproject.service.mappers.impl.UserMapper;

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

	@Spy
	private UserMapper userMapper;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	public void setUp() {
		userMapper = new UserMapper();
		userService = new UserService(userRepository, bCryptPasswordEncoder, userMapper);
	}

	@Test
	public void testThatRegisterUserWithDuplicateEmailFails() {

		UserDto userDto = TestDataUtil.getNewUserDto();
		User user = userMapper.mapToEntity(userDto);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		assertThrows(DuplicateEntityException.class, () -> userService.create(userDto));

		// Verify that userRepository methods were called
		verify(userRepository, times(1)).findByEmail(anyString());
		verify(userRepository, times(0)).findByUsername(anyString());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void testThatRegisterUserWithDuplicateUsernameFails() {

		UserDto userDto = TestDataUtil.getNewUserDto();
		User user = userMapper.mapToEntity(userDto);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

		assertThrows(DuplicateEntityException.class, () -> userService.create(userDto));

		// Verify that userRepository methods were called
		verify(userRepository, times(1)).findByEmail(anyString());
		verify(userRepository, times(1)).findByUsername(anyString());
		verify(userRepository, times(0)).save(user);
	}

}
