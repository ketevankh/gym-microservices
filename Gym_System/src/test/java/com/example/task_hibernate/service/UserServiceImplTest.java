package com.example.task_hibernate.service;

import com.example.task_hibernate.exceptions.AuthenticationFailedException;
import com.example.task_hibernate.model.User;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.serviceDTOs.UserDTO;
import com.example.task_hibernate.repository.UserRepository;
import com.example.task_hibernate.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser() {
        UserDTO userDTO = new UserDTO("John", "Doe", true);
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);
        user.setUsername("John.Doe");
        user.setPassword("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("John", createdUser.getFirstName());
        assertEquals("Doe", createdUser.getLastName());
        assertTrue(createdUser.getIsActive());
        assertEquals("John.Doe", createdUser.getUsername());
        assertEquals("encodedPassword", createdUser.getPassword());
    }

    @Test
    void getAllUsers() {
        userService.getAllUsers();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(id);

        assertTrue(foundUser.isPresent());
        assertEquals(id, foundUser.get().getId());
    }

    @Test
    void getUserByUserName() {
        String userName = "John.Doe";
        User user = new User();
        user.setUsername(userName);

        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUserName(userName);

        assertTrue(foundUser.isPresent());
        assertEquals(userName, foundUser.get().getUsername());
    }

    @Test
    void changeUserPassword() {
        String userName = "John.Doe";
        String newPassword = "newPassword";
        User user = new User();
        user.setUsername(userName);
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(newPassword, user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        boolean result = userService.changeUserPassword(newPassword, userName);

        assertTrue(result);
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changeUserPasswordAlreadyUsed() {
        String userName = "John.Doe";
        String newPassword = "newPassword";
        User user = new User();
        user.setUsername(userName);
        user.setPassword("encodedNewPassword");

        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(newPassword, user.getPassword())).thenReturn(true);

        boolean result = userService.changeUserPassword(newPassword, userName);

        assertFalse(result);
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void updateUser() {
        Long id = 1L;
        UserDTO userDTO = new UserDTO("John", "Smith", true);
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        Optional<User> updatedUser = userService.updateUser(id, userDTO);

        assertTrue(updatedUser.isPresent());
        assertEquals("John", updatedUser.get().getFirstName());
        assertEquals("Smith", updatedUser.get().getLastName());
        assertTrue(updatedUser.get().getIsActive());
    }

    @Test
    void changeActiveStatus() {
        Long id = 1L;
        Boolean isActive = true;
        User user = new User();
        user.setId(id);
        user.setIsActive(false);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        boolean result = userService.changeActiveStatus(id, isActive);

        assertTrue(result);
        assertTrue(user.getIsActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void validateUserCredentials() {
        Credentials credentials = new Credentials("John.Doe", "password");
        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername(credentials.userName())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(credentials.password(), user.getPassword())).thenReturn(true);

        boolean result = userService.validateUserCredentials(credentials);

        assertTrue(result);
    }

    @Test
    void validateUserCredentialsInvalidUsername() {
        Credentials credentials = new Credentials("John.Doe", "password");

        when(userRepository.findByUsername(credentials.userName())).thenReturn(Optional.empty());

        Exception exception = assertThrows(AuthenticationFailedException.class, () -> {
            userService.validateUserCredentials(credentials);
        });

        assertEquals("Invalid username", exception.getMessage());
    }

    @Test
    void validateUserCredentialsInvalidPassword() {
        Credentials credentials = new Credentials("John.Doe", "password");
        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername(credentials.userName())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(credentials.password(), user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(AuthenticationFailedException.class, () -> {
            userService.validateUserCredentials(credentials);
        });

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void generatePassword() {
        String password = userService.generatePassword();
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void generateUserName() {
        String firstName = "John";
        String lastName = "Doe";
        String expectedUserName = "John.Doe";

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        String userName = userService.generateUserName(firstName, lastName);

        assertNotNull(userName);
        assertEquals(expectedUserName, userName);
    }

    @Test
    void generateUserNameNotUnique() {
        String firstName = "John";
        String lastName = "Doe";
        String expectedUserName = "John.Doe1";

        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(new User()));
        when(userRepository.findByUsername("John.Doe1")).thenReturn(Optional.empty());

        String userName = userService.generateUserName(firstName, lastName);

        assertNotNull(userName);
        assertEquals(expectedUserName, userName);
    }
}
