package com.example.springproject.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.example.springproject.TestDataUtil;
import com.example.springproject.dto.UserDto;
import com.example.springproject.entity.User;
import com.example.springproject.exception.EntityNotFoundException;
import com.example.springproject.repository.UserRepository;
import com.example.springproject.service.mapper.impl.UserMapper;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTests {

    private UserService userService;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserMapper userMapper;

    @Autowired
    public UserServiceIntegrationTests(UserService userService, UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder, UserMapper userMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }

    @BeforeEach
    public void setUp() {
        userService.create(TestDataUtil.getRegisteredUser());
    }

    @Test
    public void assertThatCreateUserPasses() {

        UserDto userDto = TestDataUtil.getNewUserDto();

        long numUsersBefore = userRepository.count();
        UserDto registeredUserDto = userService.create(userDto);
        long numUsersAfter = userRepository.count();

        assertEquals(1, numUsersAfter - numUsersBefore);
        assertEquals(userDto.username(), registeredUserDto.username());
        assertEquals(userDto.email(), registeredUserDto.email());
        assertNotNull(registeredUserDto.id());
    }

    @Test
    public void assertThatUpdateUserPasses() {

        User user = userService.getByName(TestDataUtil.getRegisteredUser().username());
        UserDto userDto = TestDataUtil.getUpdatedRegisteredUserDto(user.getId());

        UserDto updatedUserDto = userService.update(user.getId(), userDto);
        User userAfterUpdate = userService.getByName(TestDataUtil.getRegisteredUser().username());

        assertEquals(userDto.email(), updatedUserDto.email());
        assertNull(updatedUserDto.password());
        assertNotEquals(user.getPassword(), userAfterUpdate.getPassword());
    }

    @Test
    public void assertThatDeleteUserPasses() {

        User user = userService.getByName(TestDataUtil.getRegisteredUser().username());

        long numUsersBefore = userRepository.count();
        userService.delete(user.getId());
        long numUsersAfter = userRepository.count();

        assertEquals(1, numUsersBefore - numUsersAfter);
        assertThrows(EntityNotFoundException.class, () -> userService.getByName(user.getUsername()));
    }

}
