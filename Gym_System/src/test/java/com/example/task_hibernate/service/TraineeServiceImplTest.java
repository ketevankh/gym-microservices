package com.example.task_hibernate.service;

import com.example.task_hibernate.exceptions.ResourceNotFoundException;
import com.example.task_hibernate.exceptions.UpdateFailedException;
import com.example.task_hibernate.model.*;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.serviceDTOs.TraineeDTO;
import com.example.task_hibernate.model.dto.serviceDTOs.UserDTO;
import com.example.task_hibernate.repository.TraineeRepository;
import com.example.task_hibernate.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private TraineeDTO traineeDTO;
    private UserDTO userDTO;
    private Credentials credentials;
    private Trainee trainee;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setIsActive(true);

        traineeDTO = new TraineeDTO();
        traineeDTO.setUser(userDTO);
        traineeDTO.setAddress("123 Main St");
        traineeDTO.setDateOfBirth(new Date());

        credentials = new Credentials("John.Doe", "password");

        user = new User();
        user.setId(1L);
        user.setUsername("John.Doe");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setUser(user);
        trainee.setId(1L);
        trainee.setAddress("321 Main St");
        trainee.setDateOfBirth(new Date());
    }

    @Test
    void createTrainee() {
        when(userService.createUser(any(UserDTO.class))).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(new Trainee());

        Trainee createdTrainee = traineeService.createTrainee(traineeDTO);

        assertNotNull(createdTrainee);
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void getAllTrainees() {
        when(traineeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Trainee> trainees = traineeService.getAllTrainees(credentials);

        assertNotNull(trainees);
        verify(traineeRepository, times(1)).findAll();
    }

    @Test
    void getTraineeById() {
        Long id = 1L;
        when(traineeRepository.findById(id)).thenReturn(Optional.of(trainee));

        Optional<Trainee> foundTrainee = traineeService.getTraineeById(id, credentials);

        assertTrue(foundTrainee.isPresent());
        assertEquals(id, foundTrainee.get().getId());
    }

    @Test
    void getTraineeByUsername() {
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));

        Optional<Trainee> foundTrainee = traineeService.getTraineeByUsername(credentials.userName(), credentials);

        assertTrue(foundTrainee.isPresent());
        assertEquals(credentials.userName(), foundTrainee.get().getUser().getUsername());
    }

    @Test
    void changeTraineePassword() {
        String newPassword = "newPassword";
        when(userService.changeUserPassword(anyString(), anyString())).thenReturn(true);

        boolean result = traineeService.changeTraineePassword(newPassword, credentials);

        assertTrue(result);
    }

    @Test
    void updateTrainee() {
        when(userService.validateUserCredentials(any(Credentials.class))).thenReturn(true);
        when(traineeRepository.findByUser_Username(anyString())).thenReturn(Optional.of(trainee));
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(Optional.of(user));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        Optional<Trainee> updatedTrainee = traineeService.updateTrainee(traineeDTO, credentials);

        assertTrue(updatedTrainee.isPresent());
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void updateTrainee_UserUpdateFailed() {
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(Optional.empty());

        assertThrows(UpdateFailedException.class, () -> traineeService.updateTrainee(traineeDTO, credentials));
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));
    }

    @Test
    void changeActiveStatus() {
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));
        when(userService.changeActiveStatus(anyLong(), anyBoolean())).thenReturn(true);

        boolean result = traineeService.changeActiveStatus(true, credentials);

        assertTrue(result);
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
        verify(userService, times(1)).changeActiveStatus(anyLong(), anyBoolean());
    }

    @Test
    void deleteTrainee() {
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));

        boolean result = traineeService.deleteTrainee(credentials.userName(), credentials);

        assertTrue(result);
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
        verify(traineeRepository, times(1)).deleteByUser_Username(credentials.userName());
    }

    @Test
    void getTrainers() {
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));
        when(trainingService.getTrainersOfTrainee(anyString())).thenReturn(Collections.emptyList());

        List<Trainer> trainers = traineeService.getTrainers(credentials.userName(), credentials);

        assertNotNull(trainers);
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
        verify(trainingService, times(1)).getTrainersOfTrainee(credentials.userName());
    }

    @Test
    void getActiveTrainersNotAssignedTo() {
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));
        when(trainerService.getAllTrainers(any(Credentials.class))).thenReturn(Collections.emptyList());
        when(trainingService.getTrainersOfTrainee(anyString())).thenReturn(Collections.emptyList());

        List<Trainer> trainers = traineeService.getActiveTrainersNotAssignedTo(credentials.userName(), credentials);

        assertNotNull(trainers);
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
        verify(trainerService, times(1)).getAllTrainers(credentials);
    }

    @Test
    void updateTrainersList() {
        List<String> trainerUsernames = new ArrayList<>();
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));
        when(trainingService.getTrainersOfTrainee(anyString())).thenReturn(Collections.emptyList());

        List<Trainer> updatedTrainers = traineeService.updateTrainersList(credentials.userName(), trainerUsernames, credentials);

        assertNotNull(updatedTrainers);
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
        verify(trainingService, times(2)).getTrainersOfTrainee(credentials.userName());
    }

    @Test
    void getTrainings() {
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));
        when(trainingService.getTraineeTrainingsList(anyString(), any(Date.class), any(Date.class), anyString(), anyString())).thenReturn(Collections.emptyList());

        List<Training> trainings = traineeService.getTrainings(credentials.userName(), new Date(), new Date(), "Bob.Johnson", "Yoga", credentials);

        assertNotNull(trainings);
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
        verify(trainingService, times(1)).getTraineeTrainingsList(anyString(), any(Date.class), any(Date.class), anyString(), anyString());
    }

    @Test
    void findTraineeByUsername() {
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));

        Boolean found = traineeService.findTraineeByUsername(credentials.userName());

        assertTrue(found);
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
    }

    @Test
    void findTraineeByUsername_NotFound() {
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.findTraineeByUsername(credentials.userName()));
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
    }
}
