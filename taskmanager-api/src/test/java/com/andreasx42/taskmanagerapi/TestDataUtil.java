package com.andreasx42.taskmanagerapi;

import com.andreasx42.taskmanagerapi.dto.TodoDto;
import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.Todo;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.security.manager.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

public final class TestDataUtil {

	private TestDataUtil() {
	}

	public static UserDto getRegisteredUser(String id) {
		String username = "Jane_Doe" + id;
		return new UserDto(null, username, username + "@gmail.com", User.Role.USER, "pw123");
	}

	public static UserDto getNewUserDto(String id) {
		String username = "John_Doe" + id;
		return new UserDto(null, username, username + "@gmail.com", User.Role.USER, "pw123");
	}

	public static UserDto getUpdatedRegisteredUserDto(String uuid, Long id) {
		return new UserDto(id,
				getRegisteredUser(uuid).username(),
				"new_address@gmail.com",
				getRegisteredUser(uuid).role(),
				"new_password");
	}

	public static TodoDto getNewTodoDto(Long userId) {
		return new TodoDto(null, userId, "my_todo", Todo.Priority.LOW, Todo.Status.OPEN, LocalDate.of(2025, 04, 29));
	}

	public static TodoDto getUpdatedTodoDto(Long todoId, Long userId) {
		return new TodoDto(todoId,
				userId,
				"my_updated_todo",
				Todo.Priority.HIGH,
				Todo.Status.FINISHED,
				LocalDate.of(2025, 3, 13));

	}

	public static TodoDto getExistingTodoForRegisteredUser(long userId) {
		return new TodoDto(null, userId, "my_old_todo", Todo.Priority.MID, Todo.Status.OPEN, LocalDate.of(2025, 3, 9));
	}

	public static void setAuthenticationContext(UserDto userDto, User.Role role) {
		Set<SimpleGrantedAuthority> authority = Collections.singleton(new SimpleGrantedAuthority(role.toString()));

		org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(userDto.id(),
				userDto.username(),
				" ",
				authority);

		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authority);
		SecurityContextHolder.getContext()
		                     .setAuthentication(auth);
	}

}
