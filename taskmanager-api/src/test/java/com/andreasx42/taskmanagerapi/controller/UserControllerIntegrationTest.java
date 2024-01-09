package com.andreasx42.taskmanagerapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.andreasx42.taskmanagerapi.TestDataUtil;
import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.exception.EntityNotFoundException;
import com.andreasx42.taskmanagerapi.repository.UserRepository;
import com.andreasx42.taskmanagerapi.security.SecurityConstants;
import com.andreasx42.taskmanagerapi.service.impl.UserService;
import com.andreasx42.taskmanagerapi.service.mapper.impl.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

        private final UserService userService;
        private final UserRepository userRepository;
        private final UserMapper userMapper;
        private final ObjectMapper objectMapper;
        private final MockMvc mockMvc;

        @Autowired
        public UserControllerIntegrationTest(UserService userService, UserRepository userRepository,
                        UserMapper userMapper,
                        ObjectMapper objectMapper,
                        MockMvc mockMvc) {
                this.userService = userService;
                this.userRepository = userRepository;
                this.userMapper = userMapper;
                this.objectMapper = objectMapper;
                this.mockMvc = mockMvc;
        }

        @BeforeEach
        public void setUp() {
                UserDto userDto = userService.create(TestDataUtil.getRegisteredUser());

                // Set authentication for user
                TestDataUtil.setAuthenticationContext(userDto, userDto.role());

        }

        @Test
        public void testRegisterUserEndpoint() throws Exception {

                UserDto userDto = TestDataUtil.getNewUserDto();
                String userDtoJson = objectMapper.writeValueAsString(userDto);

                mockMvc.perform(
                                MockMvcRequestBuilders.post(SecurityConstants.REGISTER_PATH)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(userDtoJson))
                                .andExpect(MockMvcResultMatchers.status().isCreated())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(userDto.username()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.email()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(userDto.role().toString()));

        }

        @Test
        public void testGetUserByIdEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());

                mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId()))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(user.getRole().toString()));
        }

        @Test
        public void testGetAllTodosEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());

                mockMvc.perform(MockMvcRequestBuilders.get("/users/all").param("sort", "id,desc"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].id").value(user.getId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].username")
                                                .value(user.getUsername()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].email").value(user.getEmail()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].role")
                                                .value(user.getRole().toString()));
        }

        @Test
        public void testUpdateUserEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
                UserDto updatedUserDto = TestDataUtil.getUpdatedRegisteredUserDto(user.getId());
                String updatedUserDtoJson = objectMapper.writeValueAsString(updatedUserDto);

                mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedUserDtoJson))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.username")
                                                .value(updatedUserDto.username()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updatedUserDto.email()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.role")
                                                .value(updatedUserDto.role().toString()));
        }

        @Test
        public void testDeleteUserEndpoint() throws Exception {

                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());

                mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", user.getId()))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());

                assertThrows(EntityNotFoundException.class, () -> userService.getById(user.getId()));
        }

        @Test
        public void testAdminAllowedToDeleteUser() throws Exception {

                // create new user with admin priviliges
                UserDto adminDto = TestDataUtil.getNewUserDto();
                adminDto = userService.create(adminDto);

                TestDataUtil.setAuthenticationContext(adminDto, User.Role.ADMIN);

                // test admin priviliges
                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());

                long numUsersBefore = userRepository.count();
                mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", user.getId()))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());
                long numUsersAfter = userRepository.count();

                assertThrows(EntityNotFoundException.class, () -> userService.getById(user.getId()));
                assertEquals(1, numUsersBefore - numUsersAfter);

        }

        @Test
        public void testUserNotAllowedToDeleteOthers() throws Exception {

                // create new user with normal user priviliges
                UserDto newUserDto = TestDataUtil.getNewUserDto();
                newUserDto = userService.create(newUserDto);

                TestDataUtil.setAuthenticationContext(newUserDto, User.Role.USER);

                // test user priviliges
                User user = userService.getByName(TestDataUtil.getRegisteredUser().username());

                mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", user.getId()))
                                .andExpect(MockMvcResultMatchers.status().isForbidden());

        }

}
