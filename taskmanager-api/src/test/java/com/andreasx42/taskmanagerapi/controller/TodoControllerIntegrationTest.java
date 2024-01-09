package com.andreasx42.taskmanagerapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.andreasx42.taskmanagerapi.TestDataUtil;
import com.andreasx42.taskmanagerapi.dto.TodoDto;
import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.exception.EntityNotFoundException;
import com.andreasx42.taskmanagerapi.repository.TodoRepository;
import com.andreasx42.taskmanagerapi.service.impl.TodoService;
import com.andreasx42.taskmanagerapi.service.impl.UserService;
import com.andreasx42.taskmanagerapi.service.mapper.impl.TodoMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TodoControllerIntegrationTest {

        private final TodoService todoService;
        private final TodoRepository todoRepository;
        private final TodoMapper todoMapper;
        private final ObjectMapper objectMapper;
        private final UserService userService;
        private final MockMvc mockMvc;

        @Autowired
        public TodoControllerIntegrationTest(TodoService todoService, TodoRepository todoRepository,
                        TodoMapper todoMapper,
                        ObjectMapper objectMapper,
                        UserService userService, MockMvc mockMvc) {
                this.todoService = todoService;
                this.todoRepository = todoRepository;
                this.todoMapper = todoMapper;
                this.objectMapper = objectMapper;
                this.userService = userService;
                this.mockMvc = mockMvc;

        }

        @BeforeEach
        public void setUp() {
                UserDto userDto = userService.create(TestDataUtil.getRegisteredUser());
                TodoDto todoDto = todoService.create(userDto.id(), TestDataUtil.getExistingTodoForRegisteredUser());

                TestDataUtil.setAuthenticationContext(userDto, userDto.role());
        }

        @Test
        public void testGetTodoByIdEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto todoDto = todoService.getByUserId(user.getId()).stream().findFirst().orElseThrow();

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todos/{todoId}", todoDto.id()))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                String content = result.getResponse().getContentAsString();
                TodoDto returnedTodoDto = objectMapper.readValue(content, TodoDto.class);

                assertTodoDtoValues(todoDto, returnedTodoDto);

        }

        @Test
        public void testGetAllTodosByUserIdEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto todoDto = todoService.getByUserId(user.getId()).stream().findFirst().orElseThrow();

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todos/user/{userId}", user.getId())
                                .param("page", "0")
                                .param("size", "1")
                                .param("sort", "id,desc"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                TodoDto firstResultTodoDto = extractTodoDtoFromResult(result);
                assertTodoDtoValues(todoDto, firstResultTodoDto);
        }

        @Test
        public void testGetAllTodosEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto todoDto = todoService.getByUserId(user.getId()).stream().findFirst().orElseThrow();

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todos/all")
                                .param("page", "0")
                                .param("size", "1")
                                .param("sort", "id,desc"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                TodoDto firstResultTodoDto = extractTodoDtoFromResult(result);
                assertTodoDtoValues(todoDto, firstResultTodoDto);
        }

        @Test
        public void testCreateTodoEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto newTodoDto = TestDataUtil.getNewTodoDto(user.getId());
                String newTodoDtoJson = objectMapper.writeValueAsString(newTodoDto);

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/todos/user/{userId}", user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newTodoDtoJson))
                                .andExpect(MockMvcResultMatchers.status().isCreated())
                                .andReturn();

                String content = result.getResponse().getContentAsString();
                TodoDto returnedTodoDto = objectMapper.readValue(content, TodoDto.class);

                assertTodoDtoValues(newTodoDto, returnedTodoDto);
        }

        @Test
        public void testUpdateTodoEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());

                TodoDto oldTodoDto = todoService.getByUserId(user.getId()).stream().findFirst().orElseThrow();
                TodoDto updatedTodoDto = TestDataUtil.getUpdatedTodoDto(oldTodoDto.id(), user.getId());
                String updatedTodoDtoJson = objectMapper.writeValueAsString(updatedTodoDto);

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                .put("/todos/user/{userId}/todo/{todoId}", user.getId(), oldTodoDto.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedTodoDtoJson))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                String content = result.getResponse().getContentAsString();
                TodoDto returnedTodoDto = objectMapper.readValue(content, TodoDto.class);

                assertTodoDtoValues(updatedTodoDto, returnedTodoDto);

        }

        @Test
        public void testDeleteTodoEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto userTodo = todoService.getByUserId(user.getId()).stream().findFirst().orElseThrow();

                long numTodosBefore = todoRepository.count();
                mockMvc.perform(
                                MockMvcRequestBuilders.delete("/todos/user/{userId}/todo/{todoId}", user.getId(),
                                                userTodo.id()))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());
                long numTodosAfter = todoRepository.count();

                assertEquals(1, numTodosBefore - numTodosAfter);
                assertThrows(EntityNotFoundException.class, () -> todoService.getById(userTodo.id()));
        }

        @Test
        public void testCreateTodoForOtherUserNotAllowed() throws Exception {

                UserDto newUserDto = userService.create(TestDataUtil.getNewUserDto());
                User oldUser = userService.getByName(TestDataUtil.getRegisteredUser().username());

                TodoDto newTodoDto = TestDataUtil.getNewTodoDto(oldUser.getId());
                String newTodoDtoJson = objectMapper.writeValueAsString(newTodoDto);

                // set authentication context for new user and create a new todo for old user
                TestDataUtil.setAuthenticationContext(newUserDto, User.Role.USER);

                mockMvc.perform(MockMvcRequestBuilders.post("/todos/user/{userId}", oldUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newTodoDtoJson))
                                .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        public void testUpdateTodoOfOtherUserNotAllowed() throws Exception {

                UserDto newUserDto = userService.create(TestDataUtil.getNewUserDto());
                User oldUser = userService.getByName(TestDataUtil.getRegisteredUser().username());

                TodoDto oldUserTodo = todoService.getByUserId(oldUser.getId()).stream().findFirst().orElseThrow();
                TodoDto updatedTodoDto = TestDataUtil.getUpdatedTodoDto(oldUserTodo.id(), oldUser.getId());
                String updatedTodoDtoJson = objectMapper.writeValueAsString(updatedTodoDto);

                // set authentication context for new user and create a new todo for old user
                TestDataUtil.setAuthenticationContext(newUserDto, User.Role.USER);

                mockMvc.perform(
                                MockMvcRequestBuilders
                                                .put("/todos/user/{userId}/todo/{todoId}", oldUser.getId(),
                                                                oldUserTodo.id())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(updatedTodoDtoJson))
                                .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        public void testDeleteTodoOfOtherUserNotAllowed() throws Exception {

                UserDto newUserDto = userService.create(TestDataUtil.getNewUserDto());

                User oldUser = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto oldUserTodo = todoService.getByUserId(oldUser.getId()).stream().findFirst().orElseThrow();

                // set authentication context for new user and create a new todo for old user
                TestDataUtil.setAuthenticationContext(newUserDto, User.Role.USER);

                mockMvc.perform(
                                MockMvcRequestBuilders.delete("/todos/user/{userId}/todo/{todoId}", oldUser.getId(),
                                                oldUserTodo.id()))
                                .andExpect(MockMvcResultMatchers.status().isForbidden());

        }

        @Test
        public void testDeleteTodoOfOtherUserAllowedForAdmins() throws Exception {

                UserDto newUserDto = userService.create(TestDataUtil.getNewUserDto());

                User oldUser = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto oldUserTodo = todoService.getByUserId(oldUser.getId()).stream().findFirst().orElseThrow();

                // set authentication context for new user and create a new todo for old user
                TestDataUtil.setAuthenticationContext(newUserDto, User.Role.ADMIN);

                long numTodosBefore = todoRepository.count();
                mockMvc.perform(
                                MockMvcRequestBuilders.delete("/todos/user/{userId}/todo/{todoId}", oldUser.getId(),
                                                oldUserTodo.id()))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());
                long numTodosAfter = todoRepository.count();

                assertEquals(1, numTodosBefore - numTodosAfter);
                assertThrows(EntityNotFoundException.class, () -> todoService.getById(oldUserTodo.id()));
        }

        private TodoDto extractTodoDtoFromResult(MvcResult result) throws Exception {

                String content = result.getResponse().getContentAsString();
                JsonNode rootNode = objectMapper.readTree(content); // Parse JSON into a tree structure

                // Get the 'content' node which holds the array of TodoDto
                JsonNode contentNode = rootNode.get("content");

                assertTrue(contentNode.isArray());
                assertTrue(contentNode.size() > 0);

                // Get the first TodoDto from the array
                return objectMapper.convertValue(contentNode.get(0), TodoDto.class);

        }

        private void assertTodoDtoValues(TodoDto expectedtodoDto, TodoDto resultDto) throws Exception {
                // assertEquals(expectedtodoDto.id(), resultDto.id()); expectation not known a
                // priori
                assertEquals(expectedtodoDto.userId(), resultDto.userId());
                assertEquals(expectedtodoDto.name(), resultDto.name());
                assertEquals(expectedtodoDto.priority(), resultDto.priority());
                assertEquals(expectedtodoDto.status(), resultDto.status());
                assertEquals(expectedtodoDto.untilDate(), resultDto.untilDate());
        }

}
