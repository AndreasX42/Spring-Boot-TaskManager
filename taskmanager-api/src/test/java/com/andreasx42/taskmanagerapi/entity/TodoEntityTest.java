package com.andreasx42.taskmanagerapi.entity;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

		var constraintViolationException = assertThrows(ConstraintViolationException.class,
				() -> testEntityManager.persistAndFlush(todo));

		assertTrue(constraintViolationException.getMessage()
		                                       .contains("name cannot be blank"));
	}

	@Test
	public void testCreateTodo_whenProvidedInvalidUntilDate_shouldThrowException() {

		todo.setUntilDate(LocalDate.MIN);

		var constraintViolationException = assertThrows(ConstraintViolationException.class,
				() -> testEntityManager.persistAndFlush(todo));

		assertTrue(constraintViolationException.getMessage()
		                                       .contains("The deadline must be in the future"));
	}

}