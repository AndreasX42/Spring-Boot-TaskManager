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
import com.example.springproject.service.impl.UserService;
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
        userService.create(TestDataUtil.registeredUser());
    }

    @Test
    public void assertThatCreateUserPasses() {

        int numUsersBefore = userRepository.findAll().size();
        User user = TestDataUtil.newUser();

        userService.create(user);
        int numUsersAfter = userRepository.findAll().size();

        User retrievedUser = userService.getByName("John Doe");

        assertEquals(1, numUsersAfter - numUsersBefore);
        assertEquals(TestDataUtil.newUser().getUsername(), retrievedUser.getUsername());
        assertEquals(TestDataUtil.newUser().getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void assertThatUpdateUserPasses() {

        User user = userService.getByName(TestDataUtil.registeredUser().getUsername());
        String oldUserPwd = user.getPassword();
        UserDTO userDTO = TestDataUtil.updatedRegisteredUser();

        User updatedUser = userService.update(user.getId(), userDTO);

        assertEquals(TestDataUtil.updatedRegisteredUser().email(), updatedUser.getEmail());
        assertNotEquals(oldUserPwd, updatedUser.getPassword());
    }

    @Test
    public void assertThatDeleteUserPasses() {

        int numUsersBefore = userRepository.findAll().size();
        User user = userService.getByName(TestDataUtil.registeredUser().getUsername());

        userService.delete(user.getId());
        int numUsersAfter = userRepository.findAll().size();

        assertEquals(1, numUsersBefore - numUsersAfter);
        assertThrows(EntityNotFoundException.class, () -> userService.getByName(user.getUsername()));
    }

}
