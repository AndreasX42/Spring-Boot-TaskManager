package com.andreasx42.taskmanagerapi.service.impl;

import com.andreasx42.taskmanagerapi.TestDataUtil;
import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.exception.DuplicateEntityException;
import com.andreasx42.taskmanagerapi.repository.UserRepository;
import com.andreasx42.taskmanagerapi.service.mapper.impl.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	private String id = UUID.randomUUID()
	                        .toString();

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
	public void testCreateUser_whenDuplicateEmailProvided_shouldThrowDuplicateEntityException() {

		UserDto userDto = TestDataUtil.getNewUserDto(id);
		User user = userMapper.mapToEntity(userDto);

		when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> userService.create(userDto)).isInstanceOf(DuplicateEntityException.class)
		                                                     .hasMessageContaining("user with email")
		                                                     .hasMessageContaining("does already exist");

		// Verify that userRepository methods were called
		verify(userRepository, times(1)).findByEmail(userDto.email());
		verify(userRepository, times(0)).findByUsername(anyString());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void testCreateUser_whenDuplicateUsernameProvided_shouldThrowDuplicateEntityException() {

		UserDto userDto = TestDataUtil.getNewUserDto(id);
		User user = userMapper.mapToEntity(userDto);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(userRepository.findByUsername(userDto.username())).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> userService.create(userDto)).isInstanceOf(DuplicateEntityException.class)
		                                                     .hasMessageContaining("user with username")
		                                                     .hasMessageContaining("does already exist");

		// Verify that userRepository methods were called
		verify(userRepository, times(1)).findByEmail(anyString());
		verify(userRepository, times(1)).findByUsername(anyString());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void testUpdateUser_whenNewEmailAlreadyExists_shouldThrowDuplicateEntityException() {

		UserDto userDto = TestDataUtil.getNewUserDto(id);
		User user = userMapper.mapToEntity(userDto);
		UserDto userDtoUpdated = new UserDto(userDto.id(),
				userDto.username(),
				"new_email@gmail.com",
				userDto.role(),
				null);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		when(userRepository.findByEmail(userDtoUpdated.email())).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> userService.update(1L, userDtoUpdated)).isInstanceOf(DuplicateEntityException.class)
		                                                                .hasMessageContaining("user with email")
		                                                                .hasMessageContaining("does already exist");

	}

	@Test
	public void testUpdateUser_whenNewPasswordProvided_shouldUpdatePasswordInDb() {

		UserDto userDto = TestDataUtil.getNewUserDto(id);
		User user = userMapper.mapToEntity(userDto);
		UserDto userDtoUpdated = new UserDto(userDto.id(),
				userDto.username(),
				userDto.email(),
				userDto.role(),
				"new_password");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		when(bCryptPasswordEncoder.encode(userDtoUpdated.password())).thenReturn("encoded_new_password");
		when(userRepository.save(any(User.class))).thenReturn(user);

		UserDto resultUserDto = userService.update(1L, userDtoUpdated);

		assertThat(resultUserDto.username()).isEqualTo(userDto.username());
		assertThat(resultUserDto.email()).isEqualTo(userDto.email());
		assertThat(resultUserDto.password()).isNull();
	}

}
