package com.example.springproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.springproject.entity.User;
import com.example.springproject.repository.UserRepository;
import com.example.springproject.service.UserServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc()
public class UserServiceImplTest {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private MockMvc mockMvc;

	private UserServiceImpl userService;

	@BeforeEach
	public void setUp() {
		userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);
	}

	@Test
	public void testRegisterUser() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setUsername("testUser");
		user.setPassword("password");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		User registeredUser = userService.registerUser(user);

		assert (registeredUser.getPassword()).equals("encodedPassword");
	}

}
