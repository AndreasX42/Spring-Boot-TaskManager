package com.andreasx42.taskmanagerapi.controller;

import com.andreasx42.taskmanagerapi.TestDataUtil;
import com.andreasx42.taskmanagerapi.dto.TodoDto;
import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.exception.EntityNotFoundException;
import com.andreasx42.taskmanagerapi.repository.TodoRepository;
import com.andreasx42.taskmanagerapi.security.SecurityConstants;
import com.andreasx42.taskmanagerapi.service.impl.TodoService;
import com.andreasx42.taskmanagerapi.service.impl.UserService;
import com.andreasx42.taskmanagerapi.service.mapper.impl.TodoMapper;
import com.fasterxml.jackson.databind.JsonNode;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class TodoControllerIntegrationTest {

	private final TodoService todoService;
	private final TodoMapper todoMapper;
	private final ObjectMapper objectMapper;
	private final UserService userService;
	private final MockMvc mockMvc;

	private String authorizationTokenRegisteredUser;
	private String authorizationTokenNewUser;
	private TodoDto existingTodoDto;
	private UserDto registeredUser;
	private UserDto newUser;

	@Autowired
	public TodoControllerIntegrationTest(TodoService todoService, TodoRepository todoRepository, TodoMapper todoMapper, ObjectMapper objectMapper, UserService userService, MockMvc mockMvc) {
		this.todoService = todoService;
		this.todoMapper = todoMapper;
		this.objectMapper = objectMapper;
		this.userService = userService;
		this.mockMvc = mockMvc;

	}

	@Test
	@Order(1)
	public void testCreateTodo_whenValidTodoDetailsProvided_shouldCreateTodoForUser() throws Exception {

		// create user and authenticate for jwt
		registeredUser = userService.create(TestDataUtil.getRegisteredUser());

		String userDtoJson = objectMapper.writeValueAsString(TestDataUtil.getRegisteredUser());

		authorizationTokenRegisteredUser = mockMvc.perform(MockMvcRequestBuilders.post(SecurityConstants.AUTH_PATH)
		                                                                         .contentType(MediaType.APPLICATION_JSON)
		                                                                         .content(userDtoJson))
		                                          .andExpect(MockMvcResultMatchers.status()
		                                                                          .isOk())
		                                          .andReturn()
		                                          .getResponse()
		                                          .getHeader("Authorization");

		TodoDto newTodoDto = TestDataUtil.getExistingTodoForRegisteredUser(registeredUser.id());

		String newTodoDtoJson = objectMapper.writeValueAsString(newTodoDto);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/todos/user/{userId}", registeredUser.id())
		                                                         .contentType(MediaType.APPLICATION_JSON)
		                                                         .header("Authorization",
				                                                         authorizationTokenRegisteredUser)
		                                                         .content(newTodoDtoJson))
		                          .andExpect(MockMvcResultMatchers.status()
		                                                          .isCreated())
		                          .andReturn();

		String content = result.getResponse()
		                       .getContentAsString();

		existingTodoDto = objectMapper.readValue(content, TodoDto.class);

		assertNotNull(existingTodoDto.id());
		assertEquals(newTodoDto.userId(), existingTodoDto.userId());
		assertEquals(newTodoDto.name(), existingTodoDto.name());
		assertEquals(newTodoDto.priority(), existingTodoDto.priority());
		assertEquals(newTodoDto.status(), existingTodoDto.status());
		assertEquals(newTodoDto.untilDate(), existingTodoDto.untilDate());
	}

	@Test
	@Order(2)
	public void testUpdateTodo_whenUpdatedTodoDetailsProvided_shouldPersistNewDetailsToDb() throws Exception {

		TodoDto oldTodoDto = todoMapper.mapFromEntity(todoService.getById(existingTodoDto.id()));

		TodoDto updatedTodoDto = TestDataUtil.getUpdatedTodoDto(oldTodoDto.id(), registeredUser.id());
		String updatedTodoDtoJson = objectMapper.writeValueAsString(updatedTodoDto);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/todos/user/{userId}/todo/{todoId}",
				                                                         registeredUser.id(),
				                                                         oldTodoDto.id())
		                                                         .header("Authorization",
				                                                         authorizationTokenRegisteredUser)
		                                                         .contentType(MediaType.APPLICATION_JSON)
		                                                         .content(updatedTodoDtoJson))
		                          .andExpect(MockMvcResultMatchers.status()
		                                                          .isOk())
		                          .andReturn();

		String content = result.getResponse()
		                       .getContentAsString();

		existingTodoDto = objectMapper.readValue(content, TodoDto.class);

		assertEquals(updatedTodoDto.userId(), existingTodoDto.userId());
		assertEquals(updatedTodoDto.name(), existingTodoDto.name());
		assertEquals(updatedTodoDto.priority(), existingTodoDto.priority());
		assertEquals(updatedTodoDto.status(), existingTodoDto.status());
		assertEquals(updatedTodoDto.untilDate(), existingTodoDto.untilDate());

	}

	@Test
	@Order(3)
	public void testGetTodo_whenProvidedValidTodoId_shouldReturnTodoDetails() throws Exception {

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todos/{todoId}", existingTodoDto.id())
		                                                         .header("Authorization",
				                                                         authorizationTokenRegisteredUser))
		                          .andExpect(MockMvcResultMatchers.status()
		                                                          .isOk())
		                          .andReturn();

		String content = result.getResponse()
		                       .getContentAsString();

		TodoDto resultTodoDto = objectMapper.readValue(content, TodoDto.class);

		assertEquals(existingTodoDto.userId(), resultTodoDto.userId());
		assertEquals(existingTodoDto.name(), resultTodoDto.name());
		assertEquals(existingTodoDto.priority(), resultTodoDto.priority());
		assertEquals(existingTodoDto.status(), resultTodoDto.status());
		assertEquals(existingTodoDto.untilDate(), resultTodoDto.untilDate());

	}

	@Test
	@Order(4)
	public void testGetAllTodos_whenProvidedValidUserId_shouldReturnListOfUserTodos() throws Exception {

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todos/user/{userId}", registeredUser.id())
		                                                         .param("page", "0")
		                                                         .param("size", "1")
		                                                         .param("sort", "id,desc")
		                                                         .header("Authorization",
				                                                         authorizationTokenRegisteredUser))
		                          .andExpect(MockMvcResultMatchers.status()
		                                                          .isOk())
		                          .andReturn();

		TodoDto resultTodoDto = extractTodoDtoFromResult(result);

		assertEquals(existingTodoDto.userId(), resultTodoDto.userId());
		assertEquals(existingTodoDto.name(), resultTodoDto.name());
		assertEquals(existingTodoDto.priority(), resultTodoDto.priority());
		assertEquals(existingTodoDto.status(), resultTodoDto.status());
		assertEquals(existingTodoDto.untilDate(), resultTodoDto.untilDate());
	}

	@Test
	@Order(5)
	public void testGetAllTodos_whenProvidedAdminCredentials_shouldReturnListOfAllTodosInDb() throws Exception {

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todos/all")
		                                                         .param("page", "0")
		                                                         .param("size", "1")
		                                                         .param("sort", "id,desc")
		                                                         .header("Authorization",
				                                                         authorizationTokenRegisteredUser))
		                          .andExpect(MockMvcResultMatchers.status()
		                                                          .isOk())
		                          .andReturn();

		TodoDto resultTodoDto = extractTodoDtoFromResult(result);

		assertEquals(existingTodoDto.userId(), resultTodoDto.userId());
		assertEquals(existingTodoDto.name(), resultTodoDto.name());
		assertEquals(existingTodoDto.priority(), resultTodoDto.priority());
		assertEquals(existingTodoDto.status(), resultTodoDto.status());
		assertEquals(existingTodoDto.untilDate(), resultTodoDto.untilDate());

	}

	@Test
	@Order(6)
	public void testDeleteTodo_whenProvidedValidUserIdAndTodoId_shouldDeleteCorrespondingTodoFromDb() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.delete("/todos/user/{userId}/todo/{todoId}",
				                                      registeredUser.id(),
				                                      existingTodoDto.id())
		                                      .header("Authorization", authorizationTokenRegisteredUser))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isNoContent());

		assertThrows(EntityNotFoundException.class, () -> todoService.getById(existingTodoDto.id()));
	}

	@Test
	@Order(7)
	public void testCreateTodo_whenProvidedWrongUserAuthCredentials_shouldReturnForbidden403() throws Exception {

		// persist new user to db
		newUser = userService.create(TestDataUtil.getNewUserDto());

		String newUserJson = objectMapper.writeValueAsString(TestDataUtil.getNewUserDto());

		authorizationTokenNewUser = mockMvc.perform(MockMvcRequestBuilders.post(SecurityConstants.AUTH_PATH)
		                                                                  .contentType(MediaType.APPLICATION_JSON)
		                                                                  .content(newUserJson))
		                                   .andExpect(MockMvcResultMatchers.status()
		                                                                   .isOk())
		                                   .andReturn()
		                                   .getResponse()
		                                   .getHeader("Authorization");

		TodoDto newTodoDto = TestDataUtil.getNewTodoDto(registeredUser.id());
		String newTodoDtoJson = objectMapper.writeValueAsString(newTodoDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/todos/user/{userId}", registeredUser.id())
		                                      .contentType(MediaType.APPLICATION_JSON)
		                                      .header("Authorization", authorizationTokenNewUser)
		                                      .content(newTodoDtoJson))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isForbidden());
	}

	@Test
	@Order(8)
	public void testUpdateTodo_whenRequestUpdateOtherUsersTodo_shouldReturnForbidden403() throws Exception {

		TodoDto updatedTodoDto = TestDataUtil.getUpdatedTodoDto(existingTodoDto.id(), registeredUser.id());
		String updatedTodoDtoJson = objectMapper.writeValueAsString(updatedTodoDto);

		mockMvc.perform(MockMvcRequestBuilders.put("/todos/user/{userId}/todo/{todoId}",
				                                      registeredUser.id(),
				                                      existingTodoDto.id())
		                                      .contentType(MediaType.APPLICATION_JSON)
		                                      .header("Authorization", authorizationTokenNewUser)
		                                      .content(updatedTodoDtoJson))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isForbidden());
	}

	@Test
	@Order(9)
	public void testDeleteTodo_whenRequestDeleteOtherUsersTodo_shouldReturnForbidden403() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.delete("/todos/user/{userId}/todo/{todoId}",
				                                      registeredUser.id(),
				                                      existingTodoDto.id())
		                                      .header("Authorization", authorizationTokenNewUser))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isForbidden());

	}

	@Test
	@Order(10)
	public void testDeleteTodo_whenRequestDeleteOtherUsersTodoWithAdminCredentials_shouldBeSuccessful() throws Exception {

		// set auth context as user with role admin
		TestDataUtil.setAuthenticationContext(TestDataUtil.getNewUserDto(), User.Role.ADMIN);

		mockMvc.perform(MockMvcRequestBuilders.delete("/todos/user/{userId}/todo/{todoId}",
				       registeredUser.id(),
				       existingTodoDto.id()))
		       .andExpect(MockMvcResultMatchers.status()
		                                       .isNoContent());

		assertThrows(EntityNotFoundException.class, () -> todoService.getById(existingTodoDto.id()));
	}

	private TodoDto extractTodoDtoFromResult(MvcResult result) throws Exception {

		String content = result.getResponse()
		                       .getContentAsString();
		JsonNode rootNode = objectMapper.readTree(content); // Parse JSON into a tree
		// structure

		// Get the 'content' node which holds the array of TodoDto
		JsonNode contentNode = rootNode.get("content");

		assertTrue(contentNode.isArray());
		assertTrue(contentNode.size() > 0);

		// Get the first TodoDto from the array
		return objectMapper.convertValue(contentNode.get(0), TodoDto.class);

	}

}
