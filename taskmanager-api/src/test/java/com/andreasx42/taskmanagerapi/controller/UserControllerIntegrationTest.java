package com.andreasx42.taskmanagerapi.controller;

import com.andreasx42.taskmanagerapi.TestDataUtil;
import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.exception.EntityNotFoundException;
import com.andreasx42.taskmanagerapi.repository.UserRepository;
import com.andreasx42.taskmanagerapi.security.SecurityConstants;
import com.andreasx42.taskmanagerapi.service.impl.UserService;
import com.andreasx42.taskmanagerapi.service.mapper.impl.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

	private final UserService userService;
	private final ObjectMapper objectMapper;
	private final MockMvc mockMvc;
	private UserMapper userMapper;

	private String authorizationToken;

	@Autowired
	public UserControllerIntegrationTest(UserService userService, UserRepository userRepository, UserMapper userMapper, ObjectMapper objectMapper, MockMvc mockMvc) {
		this.userService = userService;
		this.objectMapper = objectMapper;
		this.mockMvc = mockMvc;
		this.userMapper = new UserMapper();
	}

	@BeforeAll
	void setUp() {
		// set up registered user from test 1 as authenticated user
		UserDto userDto = TestDataUtil.getRegisteredUser();
		TestDataUtil.setAuthenticationContext(userDto, userDto.role());
	}

	@Test
	@Order(1)
	public void testRegisterUser_whenValidUserDetailsProvided_shouldCreateUserAndReturnUserInformation() throws Exception {

		UserDto userDto = TestDataUtil.getRegisteredUser();
		String userDtoJson = objectMapper.writeValueAsString(userDto);

		mockMvc.perform(MockMvcRequestBuilders.post(SecurityConstants.REGISTER_PATH)
		                                      .contentType(MediaType.APPLICATION_JSON)
		                                      .content(userDtoJson))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isCreated())
		       .andExpect(MockMvcResultMatchers.jsonPath("$.id")
		                                       .isNumber())
		       .andExpect(MockMvcResultMatchers.jsonPath("$.username")
		                                       .value(userDto.username()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.email")
		                                       .value(userDto.email()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.role")
		                                       .value(userDto.role()
		                                                     .toString()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.password")
		                                       .doesNotExist());

	}

	@Test
	@Order(2)
	public void testAuthenticateUser_whenCorrectUserCredentialsProvided_shouldAuthenticateAndReturnJwt() throws Exception {

		UserDto userDto = TestDataUtil.getRegisteredUser();
		String userDtoJson = objectMapper.writeValueAsString(userDto);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(SecurityConstants.AUTH_PATH)
		                                                            .contentType(MediaType.APPLICATION_JSON)
		                                                            .content(userDtoJson))
		                             .andExpect(MockMvcResultMatchers.status()
		                                                             .isOk())
		                             .andReturn();

		authorizationToken = mvcResult.getResponse()
		                              .getHeader("Authorization");

	}

	@Test
	@Order(3)
	public void testGetUserById_whenValidUserIdProvided_shouldFetchCorrespondingUserFromDb() throws Exception {

		User user = userService.getByName(TestDataUtil.getRegisteredUser()
		                                              .username());

		mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId())
		                                      .header("Authorization", authorizationToken))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isOk())
		       .andExpect(MockMvcResultMatchers.jsonPath("$.id")
		                                       .value(user.getId()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.username")
		                                       .value(user.getUsername()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.email")
		                                       .value(user.getEmail()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.role")
		                                       .value(user.getRole()
		                                                  .toString()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.password")
		                                       .doesNotExist());
	}

	@Test
	@Order(4)
	public void testGetAllUsers_whenAllUsersRequested_shouldReturnListOfAllUsers() throws Exception {

		User user = userService.getByName(TestDataUtil.getRegisteredUser()
		                                              .username());

		mockMvc.perform(MockMvcRequestBuilders.get("/users/all")
		                                      .param("sort", "id,desc")
		                                      .header("Authorization", authorizationToken))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isOk())
		       .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].id")
		                                       .value(user.getId()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].username")
		                                       .value(user.getUsername()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].email")
		                                       .value(user.getEmail()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].role")
		                                       .value(user.getRole()
		                                                  .toString()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.password")
		                                       .doesNotExist());
	}

	@Test
	@Order(5)
	public void testUpdateUser_whenProvideUpdatedValidUserDetails_shouldUpdateUserInDb() throws Exception {

		User user = userService.getByName(TestDataUtil.getRegisteredUser()
		                                              .username());
		UserDto updatedUserDto = TestDataUtil.getUpdatedRegisteredUserDto(user.getId());
		String updatedUserDtoJson = objectMapper.writeValueAsString(updatedUserDto);

		mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", user.getId())
		                                      .header("Authorization", authorizationToken)
		                                      .contentType(MediaType.APPLICATION_JSON)
		                                      .content(updatedUserDtoJson))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isOk())
		       .andExpect(MockMvcResultMatchers.jsonPath("$.id")
		                                       .value(user.getId()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.username")
		                                       .value(updatedUserDto.username()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.email")
		                                       .value(updatedUserDto.email()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.role")
		                                       .value(updatedUserDto.role()
		                                                            .toString()))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.password")
		                                       .doesNotExist());
	}

	@Test
	@Order(6)
	public void testDeleteUser_whenValidUserIdProvided_shouldDeleteCorrespondingUserFromDb() throws Exception {

		User user = userService.getByName(TestDataUtil.getRegisteredUser()
		                                              .username());

		mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", user.getId())
		                                      .header("Authorization", authorizationToken))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isNoContent());

		assertThrows(EntityNotFoundException.class, () -> userService.getByName(user.getUsername()));
	}

	@Test
	@Order(7)
	public void testDeleteUser_whenAdminCredentialsProvided_shouldBeAbleToDeleteOtherUsers() throws Exception {

		// create new user with admin priviliges
		UserDto adminDto = TestDataUtil.getNewUserDto();
		adminDto = userService.create(adminDto);

		TestDataUtil.setAuthenticationContext(adminDto, User.Role.ADMIN);

		mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", adminDto.id() - 1))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isNoContent());

	}

	@Test
	@Order(8)
	public void testDeleteUser_whenUnauthorizedUserDeletesOtherUser_shouldBeForbidden() throws Exception {

		// create new user with normal user priviliges
		User fakeAdmin = userService.getByName(TestDataUtil.getNewUserDto()
		                                                   .username());

		UserDto fakeAdminDto = userMapper.mapFromEntity(fakeAdmin);

		TestDataUtil.setAuthenticationContext(fakeAdminDto, User.Role.USER);

		mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", fakeAdminDto.id() - 1))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isForbidden());

	}

}