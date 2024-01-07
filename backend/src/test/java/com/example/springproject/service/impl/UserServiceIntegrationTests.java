package com.example.springproject.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.example.springproject.TestDataUtil;
import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.User;
import com.example.springproject.exception.EntityNotFoundException;
import com.example.springproject.repository.UserRepository;
import com.example.springproject.service.utils.UserDTOMapper;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTests {

    private UserService userService;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserDTOMapper userDTOMapper;

    @Autowired
    public UserServiceIntegrationTests(UserService userService, UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder, UserDTOMapper userDTOMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDTOMapper = userDTOMapper;
    }

    @BeforeEach
    public void setUp() {
        userService.create(TestDataUtil.getRegisteredUser());
    }

    @Test
    public void assertThatCreateUserPasses() {

        long numUsersBefore = userRepository.count();
        User user = TestDataUtil.getNewUser();

        userService.create(user);
        long numUsersAfter = userRepository.count();

        User retrievedUser = userService.getByName("John Doe");

        assertEquals(1, numUsersAfter - numUsersBefore);
        assertEquals(TestDataUtil.getNewUser().getUsername(), retrievedUser.getUsername());
        assertEquals(TestDataUtil.getNewUser().getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void assertThatUpdateUserPasses() {

        User user = userService.getByName(TestDataUtil.getRegisteredUser().getUsername());
        String oldUserPwd = user.getPassword();
        UserDTO userDTO = TestDataUtil.getUpdatedRegisteredUserDTO();

        User updatedUser = userService.update(user.getId(), userDTO);

        assertEquals(TestDataUtil.getUpdatedRegisteredUserDTO().email(), updatedUser.getEmail());
        assertNotEquals(oldUserPwd, updatedUser.getPassword());
    }

    @Test
    public void assertThatDeleteUserPasses() {

        long numUsersBefore = userRepository.count();
        User user = userService.getByName(TestDataUtil.getRegisteredUser().getUsername());

        userService.delete(user.getId());
        long numUsersAfter = userRepository.count();

        assertEquals(1, numUsersBefore - numUsersAfter);
        assertThrows(EntityNotFoundException.class, () -> userService.getByName(user.getUsername()));
    }

}
