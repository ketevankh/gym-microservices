package com.example.task_hibernate.service;

import com.example.task_hibernate.exceptions.ResourceNotFoundException;
import com.example.task_hibernate.exceptions.UpdateFailedException;
import com.example.task_hibernate.model.*;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.serviceDTOs.TrainerDTO;
import com.example.task_hibernate.model.dto.serviceDTOs.UserDTO;
import com.example.task_hibernate.repository.TrainerRepository;
import com.example.task_hibernate.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private TrainerDTO trainerDTO;
    private UserDTO userDTO;
    private Credentials credentials;
    private Trainer trainer;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setIsActive(true);

        trainerDTO = new TrainerDTO();
        trainerDTO.setUser(userDTO);
        trainerDTO.setSpecialization(new TrainingType());

        credentials = new Credentials("John.Doe", "password");

        user = new User();
        user.setId(1L);
        user.setUsername("John.Doe");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);

        trainer = new Trainer();
        trainer.setUser(user);
        trainer.setId(1L);
        trainer.setTrainingType(new TrainingType());
    }

    @Test
    void createTrainer() {
        when(userService.createUser(any(UserDTO.class))).thenReturn(user);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(new Trainer());

        Trainer createdTrainer = trainerService.createTrainer(trainerDTO);

        assertNotNull(createdTrainer);
        verify(userService, times(1)).createUser(any(UserDTO.class));
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void getAllTrainers() {
        when(trainerRepository.findAll()).thenReturn(Collections.emptyList());

        List<Trainer> trainers = trainerService.getAllTrainers(credentials);

        assertNotNull(trainers);
        verify(trainerRepository, times(1)).findAll();
    }

    @Test
    void getTrainerById() {
        Long id = 1L;
        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));

        Optional<Trainer> foundTrainer = trainerService.getTrainerById(id, credentials);

        assertTrue(foundTrainer.isPresent());
        assertEquals(id, foundTrainer.get().getId());
    }

    @Test
    void getTrainerByUserName() {
        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainer));

        Optional<Trainer> foundTrainer = trainerService.getTrainerByUserName(credentials.userName(), credentials);

        assertTrue(foundTrainer.isPresent());
        assertEquals(credentials.userName(), foundTrainer.get().getUser().getUsername());
    }

    @Test
    void changeTrainerPassword() {
        String newPassword = "newPassword";
        when(userService.changeUserPassword(anyString(), anyString())).thenReturn(true);

        boolean result = trainerService.changeTrainerPassword(newPassword, credentials);

        assertTrue(result);
    }

    @Test
    void updateTrainer() {
        when(trainerRepository.findByUser_Username(anyString())).thenReturn(Optional.of(trainer));
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(Optional.of(user));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        Optional<Trainer> updatedTrainer = trainerService.updateTrainer(trainerDTO, credentials);

        assertTrue(updatedTrainer.isPresent());
        verify(trainerRepository, times(1)).findByUser_Username(credentials.userName());
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void updateTrainer_UserUpdateFailed() {
        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainer));
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(Optional.empty());

        assertThrows(UpdateFailedException.class, () -> trainerService.updateTrainer(trainerDTO, credentials));
        verify(trainerRepository, times(1)).findByUser_Username(credentials.userName());
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));
    }

    @Test
    void changeActiveStatus() {
        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainer));
        when(userService.changeActiveStatus(anyLong(), anyBoolean())).thenReturn(true);

        boolean result = trainerService.changeActiveStatus(true, credentials);

        assertTrue(result);
        verify(trainerRepository, times(1)).findByUser_Username(credentials.userName());
        verify(userService, times(1)).changeActiveStatus(anyLong(), anyBoolean());
    }

    @Test
    void deleteTrainer() {
        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainer));

        boolean result = trainerService.deleteTrainer(credentials.userName(), credentials);

        assertTrue(result);
        verify(trainerRepository, times(1)).deleteByUser_Username(credentials.userName());
    }

    @Test
    void getTrainees() {
        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainer));
        when(trainingService.getTraineesOfTrainer(anyString())).thenReturn(Collections.emptyList());

        List<Trainee> trainees = trainerService.getTrainees(credentials.userName(), credentials);

        assertNotNull(trainees);
        verify(trainingService, times(1)).getTraineesOfTrainer(credentials.userName());
    }

    @Test
    void getTrainings() {
        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainer));
        when(trainingService.getTrainerTrainingsList(anyString(), any(Date.class), any(Date.class), anyString())).thenReturn(Collections.emptyList());

        List<Training> trainings = trainerService.getTrainings(credentials.userName(), new Date(), new Date(), "Bob.Johnson", "Yoga", credentials);

        assertNotNull(trainings);
        verify(trainingService, times(1)).getTrainerTrainingsList(anyString(), any(Date.class), any(Date.class), anyString());
    }

    @Test
    void findTrainerWithUsername_NotFound() {
        String username = "John.Doe";
        when(trainerRepository.findByUser_Username(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.findTrainerWithUsername(username));
        verify(trainerRepository, times(1)).findByUser_Username(username);
    }
}