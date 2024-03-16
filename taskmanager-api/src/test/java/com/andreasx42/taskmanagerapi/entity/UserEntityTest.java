package com.andreasx42.taskmanagerapi.entity;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserEntityTest {

	User user;

	@Autowired
	private TestEntityManager testEntityManager;

	@BeforeEach
	void setup() {
		user = new User();
		user.setUsername("username");
		user.setEmail("test@test.com");
		user.setPassword("12345678");
	}

	@Test
	void testUserEntity_whenInvalidUsernameProvided_shouldNotSaveEntity() {

		user.setUsername("");

		var constraintViolationException = assertThrows(ConstraintViolationException.class, () -> {
			testEntityManager.persistAndFlush(user);
		});

		assertTrue(constraintViolationException.getMessage()
		                                       .contains("username cannot be blank"));
	}

	@Test
	void testUserEntity_whenInvalidEmailProvided_shouldNotSaveEntity() {

		user.setEmail("");

		var constraintViolationException = assertThrows(ConstraintViolationException.class, () -> {
			testEntityManager.persistAndFlush(user);
		});

		assertTrue(constraintViolationException.getMessage()
		                                       .contains("email cannot be blank"));
	}

	@Test
	void testUserEntity_whenInvalidPasswordProvided_shouldNotSaveEntity() {

		user.setPassword("");

		var constraintViolationException = assertThrows(ConstraintViolationException.class, () -> {
			testEntityManager.persistAndFlush(user);
		});

		assertTrue(constraintViolationException.getMessage()
		                                       .contains("password cannot be blank"));
	}

}
