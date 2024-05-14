package com.example.task_hibernate.service;

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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
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
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setIsActive(userDTO.getIsActive());
        user.setUsername(userService.generateUserName(userDTO.getFirstName(), userDTO.getLastName()));
        user.setPassword(userService.generatePassword());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User arg = invocation.getArgument(0);
            assertEquals(user.getFirstName(), arg.getFirstName());
            assertEquals(user.getLastName(), arg.getLastName());
            assertEquals(user.getIsActive(), arg.getIsActive());
            assertEquals(user.getUsername(), arg.getUsername());
            assertTrue(arg.getPassword().matches("^[a-zA-Z0-9]*$"));
            return user;
        });

        User createdUser = userService.createUser(userDTO);
        assertNotNull(createdUser);
        assertEquals(user.getFirstName(), createdUser.getFirstName());
        assertEquals(user.getLastName(), createdUser.getLastName());
        assertEquals(user.getIsActive(), createdUser.getIsActive());
        assertEquals(user.getUsername(), createdUser.getUsername());
        assertTrue(createdUser.getPassword().matches("^[a-zA-Z0-9]*$"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(new User()));
        List<User> users = userService.getAllUsers();
        assertFalse(users.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserById(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository, times(1)).findById(userId);

        long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        result = userService.getUserById(nonExistentUserId);
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(nonExistentUserId);
    }

    @Test
    void generateUserName() {
        String firstName = "John";
        String lastName = "Doe";
        String expectedUserName = firstName + "." + lastName;

        when(userRepository.findByUsername(expectedUserName)).thenReturn(Optional.empty());
        String userName = userService.generateUserName(firstName, lastName);
        assertEquals(expectedUserName, userName);
        verify(userRepository, times(1)).findByUsername(expectedUserName);

        User user = new User();
        user.setUsername(expectedUserName);
        when(userRepository.findByUsername(expectedUserName)).thenReturn(Optional.of(user));
        userName = userService.generateUserName(firstName, lastName);
        assertNotEquals(expectedUserName, userName);
        verify(userRepository, times(2)).findByUsername(expectedUserName);
    }

    @Test
    void changeActiveStatus() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setIsActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        boolean result = userService.changeActiveStatus(userId, true);
        assertTrue(result);
        assertTrue(user.getIsActive());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);

        result = userService.changeActiveStatus(userId, true);
        assertFalse(result);
        assertTrue(user.getIsActive());
        verify(userRepository, times(2)).findById(userId);
        verify(userRepository, times(1)).save(user);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        result = userService.changeActiveStatus(userId, false);
        assertFalse(result);
        verify(userRepository, times(3)).findById(userId);
    }

    @Test
    void updateUser() {

        Long nonExistentUserId = 999L;
        UserDTO userDTO = new UserDTO("John", "Doe", true);
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        Optional<User> result = userService.updateUser(nonExistentUserId, userDTO);
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(nonExistentUserId);

        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("OldFirstName");
        user.setLastName("OldLastName");
        user.setIsActive(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User arg = invocation.getArgument(0);
            assertEquals(userDTO.getFirstName(), arg.getFirstName());
            assertEquals(userDTO.getLastName(), arg.getLastName());
            assertEquals(userDTO.getIsActive(), arg.getIsActive());
            return arg;
        });
        result = userService.updateUser(userId, userDTO);
        assertTrue(result.isPresent());
        assertEquals(userDTO.getFirstName(), result.get().getFirstName());
        assertEquals(userDTO.getLastName(), result.get().getLastName());
        assertEquals(userDTO.getIsActive(), result.get().getIsActive());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void changeUserPassword() {
        String userName = "John.Doe";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        User user = new User();
        user.setUsername(userName);
        user.setPassword(oldPassword);

        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        boolean result = userService.changeUserPassword(newPassword, userName);
        assertTrue(result);
        assertEquals(newPassword, user.getPassword());
        verify(userRepository, times(1)).findByUsername(userName);
        verify(userRepository, times(1)).save(user);

        result = userService.changeUserPassword(newPassword, userName);
        assertFalse(result);
        assertEquals(newPassword, user.getPassword());
        verify(userRepository, times(2)).findByUsername(userName);
        verify(userRepository, times(1)).save(user);

        when(userRepository.findByUsername(userName)).thenReturn(Optional.empty());
        result = userService.changeUserPassword(newPassword, userName);
        assertFalse(result);
        verify(userRepository, times(3)).findByUsername(userName);
    }

    @Test
    void getUserByUserName() {
        String userName = "John.Doe";
        User user = new User();
        user.setUsername(userName);

        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserByUserName(userName);
        assertTrue(result.isPresent());
        assertEquals(userName, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(userName);

        when(userRepository.findByUsername(userName)).thenReturn(Optional.empty());
        result = userService.getUserByUserName(userName);
        assertFalse(result.isPresent());
        verify(userRepository, times(2)).findByUsername(userName);
    }

    @Test
    void deleteUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        doNothing().when(userRepository).deleteById(userId);
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void validateUserFailed() {
        String userName = "John.Doe";
        String password = "password";
        Credentials credentials = new Credentials(userName, password);
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);


        when(userRepository.findByUsername(userName)).thenReturn(Optional.empty());
        boolean result = userService.validateUserCredentials(credentials);
        assertTrue(result);
        verify(userRepository, times(1)).findByUsername(userName);

        credentials = new Credentials(userName, "wrongPassword");
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        result = userService.validateUserCredentials(credentials);
        assertTrue(result);
        verify(userRepository, times(2)).findByUsername(userName);

        credentials = new Credentials(userName, password);
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        result = userService.validateUserCredentials(credentials);
        assertFalse(result);
        verify(userRepository, times(3)).findByUsername(userName);
    }
}
