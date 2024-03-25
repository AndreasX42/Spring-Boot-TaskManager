package com.andreasx42.taskmanagerapi.entity;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class TodoEntityTest {

	User user;
	Todo todo;

	@Autowired
	private TestEntityManager testEntityManager;

	@BeforeEach
	void setup() {
		user = new User();
		user.setUsername("username");
		user.setEmail("test@test.com");
		user.setPassword("12345678");

		user = testEntityManager.persistAndFlush(user);

		todo = new Todo();
		todo.setName("name");
		todo.setUser(user);
		todo.setPriority(Todo.Priority.MID);
		todo.setStatus(Todo.Status.OPEN);
		todo.setUntilDate(LocalDate.MAX);

	}

	@Test
	public void testCreateTodo_whenProvidedInvalidName_shouldThrowException() {

		todo.setName("");

		assertThatThrownBy(() -> testEntityManager.persistAndFlush(todo)).isInstanceOf(ConstraintViolationException.class)
		                                                                 .hasMessageContaining("name cannot be blank");

	}

	@Test
	public void testCreateTodo_whenProvidedInvalidUntilDate_shouldThrowException() {

		todo.setUntilDate(LocalDate.MIN);

		assertThatThrownBy(() -> testEntityManager.persistAndFlush(todo)).isInstanceOf(ConstraintViolationException.class)
		                                                                 .hasMessageContaining(
				                                                                 "The deadline must be in the future");

	}

}