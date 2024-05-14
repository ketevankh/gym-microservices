package com.example.task_hibernate.service;

import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.User;
import com.example.task_hibernate.model.dto.serviceDTOs.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUserName(String userName);

    User createUser(UserDTO userDTO);

    boolean changeUserPassword(String password, String userName);

    Optional<User> updateUser(Long id, UserDTO userDTO);

    boolean changeActiveStatus(Long id, Boolean isActive);

    void deleteUser(Long id);

    String generatePassword();

    String generateUserName(String firstName, String lastName);

    boolean validateUserCredentials(Credentials credentials);

}
