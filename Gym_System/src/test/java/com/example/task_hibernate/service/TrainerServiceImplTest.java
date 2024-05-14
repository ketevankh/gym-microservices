package com.example.task_hibernate.service;

import com.example.task_hibernate.model.*;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.serviceDTOs.TrainerDTO;
import com.example.task_hibernate.model.dto.serviceDTOs.UserDTO;
import com.example.task_hibernate.model.enums.ExerciseType;
import com.example.task_hibernate.repository.TrainerRepository;
import com.example.task_hibernate.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import java.util.*;

public class TrainerServiceImplTest {
    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTrainers() {
        Credentials credentials = new Credentials("username", "password");

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertTrue(trainerService.getAllTrainers(credentials).isEmpty());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(trainerRepository.findAll()).thenReturn(Collections.singletonList(new Trainer()));
        assertFalse(trainerService.getAllTrainers(credentials).isEmpty());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(trainerRepository, times(1)).findAll();
    }

    @Test
    void getTrainerByUserName() {
        Credentials credentials = new Credentials("username", "password");
        String userName = "testUser";

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertFalse(trainerService.getTrainerByUserName(userName, credentials).isPresent());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(trainerRepository.findByUser_Username(userName)).thenReturn(Optional.of(new Trainer()));
        assertTrue(trainerService.getTrainerByUserName(userName, credentials).isPresent());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(trainerRepository, times(1)).findByUser_Username(userName);
    }

    @Test
    void createTrainer() {
        UserDTO userDTO = new UserDTO("John", "Doe", true);

        TrainingType specialization = new TrainingType(1L, ExerciseType.CARDIO);

        TrainerDTO trainerDTO = new TrainerDTO(specialization, userDTO);

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setIsActive(userDTO.getIsActive());

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setTrainingType(specialization);

        when(userService.createUser(any(UserDTO.class))).thenReturn(user);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        Trainer result = trainerService.createTrainer(trainerDTO);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(specialization, result.getTrainingType());

        verify(userService, times(1)).createUser(any(UserDTO.class));
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void getTrainerById() {
        Credentials credentials = new Credentials("username", "password");
        Long id = 1L;

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertTrue(trainerService.getTrainerById(id, credentials).isEmpty());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(trainerRepository.findById(id)).thenReturn(Optional.empty());
        assertTrue(trainerService.getTrainerById(id, credentials).isEmpty());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(trainerRepository, times(1)).findById(id);

        Trainer trainer = new Trainer();
        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));
        Optional<Trainer> result = trainerService.getTrainerById(id, credentials);
        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(trainerRepository, times(2)).findById(id);
    }

    @Test
    void changeTrainerPassword() {
        Credentials credentials = new Credentials("username", "password");
        String newPassword = "newPassword";

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertFalse(trainerService.changeTrainerPassword(newPassword, credentials));
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(userService.changeUserPassword(anyString(), anyString())).thenReturn(false);
        assertFalse(trainerService.changeTrainerPassword(newPassword, credentials));
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(userService, times(1)).changeUserPassword(anyString(), anyString());


        when(userService.changeUserPassword(anyString(), anyString())).thenReturn(true);
        assertTrue(trainerService.changeTrainerPassword(newPassword, credentials));
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(userService, times(2)).changeUserPassword(anyString(), anyString());
    }

    @Test
    void updateTrainer() {
        Credentials credentials = new Credentials("username", "password");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);
        trainer.setUser(user);

        TrainingType specialization = new TrainingType(1L, ExerciseType.CARDIO);

        TrainerDTO trainerDTO = new TrainerDTO(specialization, new UserDTO("John", "Doe", true));

        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setId(1L);
        updatedTrainer.setUser(user);

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        Optional<Trainer> result = trainerService.updateTrainer(trainerDTO, credentials);
        assertFalse(result.isPresent());

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainer));
        UserDTO updateUserDTO = new UserDTO(trainer.getUser().getFirstName(), trainer.getUser().getLastName(), trainer.getUser().getIsActive());
        when(userService.updateUser(trainer.getUser().getId(), updateUserDTO)).thenReturn(Optional.empty());
        result = trainerService.updateTrainer(trainerDTO, credentials);
        assertFalse(result.isPresent());

        when(userService.updateUser(trainer.getUser().getId(), updateUserDTO)).thenReturn(Optional.of(user));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(updatedTrainer);
        result = trainerService.updateTrainer(trainerDTO, credentials);
        assertTrue(result.isPresent());
        assertEquals(updatedTrainer, result.get());

        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(trainerRepository, times(2)).findByUser_Username(credentials.userName());
        verify(userService, times(2)).updateUser(trainer.getUser().getId(), updateUserDTO);
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void changeActiveStatus() {
        Credentials credentials = new Credentials("username", "password");
        Boolean isActive = true;

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertFalse(trainerService.changeActiveStatus(isActive, credentials));
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(new Trainer()));
        when(userService.changeActiveStatus(anyLong(), anyBoolean())).thenReturn(false);
        assertFalse(trainerService.changeActiveStatus(isActive, credentials));
        verify(userService, times(2)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(userService.changeActiveStatus(anyLong(), anyBoolean())).thenReturn(true);
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername(credentials.userName());
        user.setId(1L);
        trainer.setUser(user);
        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainer));
        when(trainerRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainer));
        assertTrue(trainerService.changeActiveStatus(isActive, credentials));
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(userService, times(1)).changeActiveStatus(anyLong(), anyBoolean());
    }

    @Test
    void deleteTrainer() {
        Credentials credentials = new Credentials("username", "password");
        String userName = "testUser";

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertFalse(trainerService.deleteTrainer(userName, credentials));
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(trainerRepository.findByUser_Username(userName)).thenReturn(Optional.empty());
        assertFalse(trainerService.deleteTrainer(userName, credentials));
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(trainerRepository, times(1)).findByUser_Username(userName);

        when(trainerRepository.findByUser_Username(userName)).thenReturn(Optional.of(new Trainer()));
        assertTrue(trainerService.deleteTrainer(userName, credentials));
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(trainerRepository, times(2)).findByUser_Username(userName);
        verify(trainerRepository, times(1)).deleteByUser_Username(userName);
    }

    @Test
    public void getTrainings() {
        Credentials credentials = new Credentials("username", "password");
        String userName = "testUser";
        Date from = new Date();
        Date to = new Date();
        String trainerUserName = "trainer";
        ExerciseType trainingType = ExerciseType.valueOf("CARDIO");

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertTrue(trainerService.getTrainings(userName, from, to, trainerUserName, trainingType.name(), credentials).isEmpty());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(trainerRepository.findByUser_Username(userName)).thenReturn(Optional.empty());
        assertTrue(trainerService.getTrainings(userName, from, to, trainerUserName, trainingType.name(), credentials).isEmpty());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(trainerRepository, times(1)).findByUser_Username(userName);

        when(trainerRepository.findByUser_Username(userName)).thenReturn(Optional.of(new Trainer()));
        when(trainingService.getTrainerTrainingsList(userName, from, to, trainerUserName)).thenReturn(Collections.emptyList());
        assertTrue(trainerService.getTrainings(userName, from, to, trainerUserName, trainingType.name(), credentials).isEmpty());
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(trainerRepository, times(2)).findByUser_Username(userName);
        verify(trainingService, times(1)).getTrainerTrainingsList(userName, from, to, trainerUserName);

        when(trainingService.getTrainerTrainingsList(userName, from, to, trainerUserName)).thenReturn(Collections.singletonList(null));
        assertFalse(trainerService.getTrainings(userName, from, to, trainerUserName, trainingType.name(), credentials).isEmpty());
        verify(userService, times(4)).validateUserCredentials(credentials);
        verify(trainerRepository, times(3)).findByUser_Username(userName);
        verify(trainingService, times(2)).getTrainerTrainingsList(userName, from, to, trainerUserName);
    }

    @Test
    public void getTrainees() {
        Credentials credentials = new Credentials("username", "password");
        String userName = "testUser";

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertTrue(trainerService.getTrainees(userName, credentials).isEmpty());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(trainerRepository.findByUser_Username(userName)).thenReturn(Optional.empty());
        assertTrue(trainerService.getTrainees(userName, credentials).isEmpty());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(trainerRepository, times(1)).findByUser_Username(userName);

        when(trainerRepository.findByUser_Username(userName)).thenReturn(Optional.of(new Trainer()));
        when(trainingService.getTraineesOfTrainer(userName)).thenReturn(Collections.emptyList());
        assertTrue(trainerService.getTrainees(userName, credentials).isEmpty());
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(trainerRepository, times(2)).findByUser_Username(userName);
        verify(trainingService, times(1)).getTraineesOfTrainer(userName);

        when(trainingService.getTraineesOfTrainer(userName)).thenReturn(Collections.singletonList(null));
        assertFalse(trainerService.getTrainees(userName, credentials).isEmpty());
        verify(userService, times(4)).validateUserCredentials(credentials);
        verify(trainerRepository, times(3)).findByUser_Username(userName);
        verify(trainingService, times(2)).getTraineesOfTrainer(userName);
    }
}
