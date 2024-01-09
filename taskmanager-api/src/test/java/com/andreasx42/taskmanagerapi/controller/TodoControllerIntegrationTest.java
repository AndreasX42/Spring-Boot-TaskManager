package com.andreasx42.taskmanagerapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.andreasx42.taskmanagerapi.TestDataUtil;
import com.andreasx42.taskmanagerapi.dto.TodoDto;
import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.exception.EntityNotFoundException;
import com.andreasx42.taskmanagerapi.repository.TodoRepository;
import com.andreasx42.taskmanagerapi.security.manager.CustomUserDetails;
import com.andreasx42.taskmanagerapi.service.impl.TodoService;
import com.andreasx42.taskmanagerapi.service.impl.UserService;
import com.andreasx42.taskmanagerapi.service.mapper.impl.TodoMapper;
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

                // Set authentication for user
                Set<SimpleGrantedAuthority> authority = Collections
                                .singleton(new SimpleGrantedAuthority(userDto.role().toString()));

                org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(userDto.id(),
                                userDto.username(), " ",
                                authority);

                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authority);
                SecurityContextHolder.getContext().setAuthentication(auth);

        }

        @Test
        public void testGetTodoByIdEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto todoDto = todoService.getByUserId(user.getId()).stream().findFirst().orElseThrow();

                mockMvc.perform(MockMvcRequestBuilders.get("/todos/{todoId}", todoDto.id()))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(todoDto.id()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(todoDto.userId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(todoDto.name()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.priority")
                                                .value(todoDto.priority().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                                .value(todoDto.status().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.untilDate")
                                                .value(todoDto.untilDate().toString()));

        }

        @Test
        public void testGetAllTodosEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto todoDto = todoService.getByUserId(user.getId()).stream().findFirst().orElseThrow();

                mockMvc.perform(MockMvcRequestBuilders.get("/todos/all").param("sort", "id,desc"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].id").value(todoDto.id()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].userId")
                                                .value(todoDto.userId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].name").value(todoDto.name()))
                                .andExpect(
                                                MockMvcResultMatchers.jsonPath("$.content.[0].priority")
                                                                .value(todoDto.priority().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].status")
                                                .value(todoDto.status().toString()))
                                .andExpect(
                                                MockMvcResultMatchers.jsonPath("$.content.[0].untilDate")
                                                                .value(todoDto.untilDate().toString()));
        }

        @Test
        public void testCreateTodoEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
                TodoDto newTodoDto = TestDataUtil.getNewTodoDto();
                String newTodoDtoJson = objectMapper.writeValueAsString(newTodoDto);

                mockMvc.perform(MockMvcRequestBuilders.post("/todos/user/{userId}", user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newTodoDtoJson))
                                .andExpect(MockMvcResultMatchers.status().isCreated())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(user.getId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newTodoDto.name()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.priority")
                                                .value(newTodoDto.priority().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                                .value(newTodoDto.status().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.untilDate")
                                                .value(newTodoDto.untilDate().toString()));
        }

        @Test
        public void testUpdateTodoEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());

                TodoDto oldTodo = todoService.getByUserId(user.getId()).stream().findFirst().orElseThrow();
                TodoDto updatedTodoDto = TestDataUtil.getUpdatedTodoDto(oldTodo.id(), user.getId());
                String updatedTodoDtoJson = objectMapper.writeValueAsString(updatedTodoDto);

                mockMvc.perform(MockMvcRequestBuilders
                                .put("/todos/user/{userId}/todo/{todoId}", user.getId(), oldTodo.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedTodoDtoJson))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(oldTodo.id()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(oldTodo.userId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updatedTodoDto.name()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.priority")
                                                .value(updatedTodoDto.priority().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                                                .value(updatedTodoDto.status().toString()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.untilDate")
                                                .value(updatedTodoDto.untilDate().toString()));
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

                TodoDto newTodoDto = TestDataUtil.getNewTodoDto();
                String newTodoDtoJson = objectMapper.writeValueAsString(newTodoDto);

                // set authentication context for new user and create a new todo for old user
                Set<SimpleGrantedAuthority> authority = Collections
                                .singleton(new SimpleGrantedAuthority(User.Role.USER.toString()));

                org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(newUserDto.id(),
                                newUserDto.username(), " ",
                                authority);

                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authority);
                SecurityContextHolder.getContext().setAuthentication(auth);

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
                Set<SimpleGrantedAuthority> authority = Collections
                                .singleton(new SimpleGrantedAuthority(User.Role.USER.toString()));

                org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(newUserDto.id(),
                                newUserDto.username(), " ",
                                authority);

                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authority);
                SecurityContextHolder.getContext().setAuthentication(auth);

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
                Set<SimpleGrantedAuthority> authority = Collections
                                .singleton(new SimpleGrantedAuthority(User.Role.USER.toString()));

                org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(newUserDto.id(),
                                newUserDto.username(), " ",
                                authority);

                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authority);
                SecurityContextHolder.getContext().setAuthentication(auth);

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
                Set<SimpleGrantedAuthority> authority = Collections
                                .singleton(new SimpleGrantedAuthority(User.Role.ADMIN.toString()));

                org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(newUserDto.id(),
                                newUserDto.username(), " ",
                                authority);

                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authority);
                SecurityContextHolder.getContext().setAuthentication(auth);

                long numTodosBefore = todoRepository.count();
                mockMvc.perform(
                                MockMvcRequestBuilders.delete("/todos/user/{userId}/todo/{todoId}", oldUser.getId(),
                                                oldUserTodo.id()))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());
                long numTodosAfter = todoRepository.count();

                assertEquals(1, numTodosBefore - numTodosAfter);
                assertThrows(EntityNotFoundException.class, () -> todoService.getById(oldUserTodo.id()));
        }

}
