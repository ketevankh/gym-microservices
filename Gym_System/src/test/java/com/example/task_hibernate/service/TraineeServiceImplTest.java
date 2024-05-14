package com.example.task_hibernate.service;

import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.User;
import com.example.task_hibernate.model.dto.serviceDTOs.TraineeDTO;
import com.example.task_hibernate.model.dto.serviceDTOs.UserDTO;
import com.example.task_hibernate.model.enums.ExerciseType;
import com.example.task_hibernate.repository.TraineeRepository;
import com.example.task_hibernate.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TraineeServiceImplTest {
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainee() throws ParseException {
        String address = "Address";
        Date dateOfBirth = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2000");
        UserDTO user = new UserDTO("John", "Doe", true);
        TraineeDTO traineeDTO = new TraineeDTO(address, dateOfBirth, user);

        when(userService.createUser(any())).thenReturn(new User());
        when(traineeRepository.save(any())).thenReturn(new Trainee());

        Trainee createdTrainee = traineeService.createTrainee(traineeDTO);

        assertNotNull(createdTrainee);
        verify(userService, times(1)).createUser(any());
        verify(traineeRepository, times(1)).save(any());
    }


    @Test
    void getTraineeById() {
        Credentials credentials = new Credentials("username", "password");
        long traineeId = 1L;
        Trainee trainee = new Trainee();
        trainee.setId(traineeId);

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        Optional<Trainee> result = traineeService.getTraineeById(traineeId, credentials);
        assertFalse(result.isPresent());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
        result = traineeService.getTraineeById(traineeId, credentials);
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(traineeRepository, times(1)).findById(traineeId);

        when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());
        result = traineeService.getTraineeById(traineeId, credentials);
        assertFalse(result.isPresent());
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(traineeRepository, times(2)).findById(traineeId);
    }

    @Test
    void updateTrainee() {
        Credentials credentials = new Credentials("username", "password");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);
        trainee.setUser(user);

        TraineeDTO traineeDTO = new TraineeDTO("Address", new Date(), new UserDTO("John", "Doe", true));

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setId(1L);
        updatedTrainee.setUser(user);

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        Optional<Trainee> result = traineeService.updateTrainee(traineeDTO, credentials);
        assertFalse(result.isPresent());

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));
        UserDTO updateUserDTO = new UserDTO(trainee.getUser().getFirstName(), trainee.getUser().getLastName(), trainee.getUser().getIsActive());
        when(userService.updateUser(trainee.getUser().getId(), updateUserDTO)).thenReturn(Optional.empty());
        result = traineeService.updateTrainee(traineeDTO, credentials);
        assertFalse(result.isPresent());

        when(userService.updateUser(trainee.getUser().getId(), updateUserDTO)).thenReturn(Optional.of(user));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(updatedTrainee);
        result = traineeService.updateTrainee(traineeDTO, credentials);
        assertTrue(result.isPresent());
        assertEquals(updatedTrainee, result.get());

        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(traineeRepository, times(2)).findByUser_Username(credentials.userName());
        verify(userService, times(2)).updateUser(trainee.getUser().getId(), updateUserDTO);
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void getAllTrainees() {
        Credentials credentials = new Credentials("username", "password");

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertTrue(traineeService.getAllTrainees(credentials).isEmpty());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findAll()).thenReturn(Collections.singletonList(new Trainee()));
        assertFalse(traineeService.getAllTrainees(credentials).isEmpty());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(traineeRepository, times(1)).findAll();
    }

    @Test
    void getTraineeByUserName() {
        Credentials credentials = new Credentials("username", "password");
        String userName = "testUser";

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertFalse(traineeService.getTraineeByUsername(userName, credentials).isPresent());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.of(new Trainee()));
        assertTrue(traineeService.getTraineeByUsername(userName, credentials).isPresent());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(traineeRepository, times(1)).findByUser_Username(userName);
    }

    @Test
    void changeTraineePassword() {
        Credentials credentials = new Credentials("username", "password");
        String newPassword = "newPassword";

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertFalse(traineeService.changeTraineePassword(newPassword, credentials));
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(userService.changeUserPassword(credentials.userName(), newPassword)).thenReturn(true);
        assertTrue(traineeService.changeTraineePassword(newPassword, credentials));
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(userService, times(1)).changeUserPassword(credentials.userName(), newPassword);
    }

    @Test
    void changeActiveStatus() {
        Credentials credentials = new Credentials("username", "password");
        boolean isActive = true;
        Trainee trainee = new Trainee();
        User user = new User();
        user.setIsActive(false);
        trainee.setUser(user);

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertFalse(traineeService.changeActiveStatus(isActive, credentials));
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findByUser_Username(credentials.userName())).thenReturn(Optional.of(trainee));
        when(userService.changeActiveStatus(trainee.getUser().getId(), isActive)).thenReturn(true);
        assertTrue(traineeService.changeActiveStatus(isActive, credentials));
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(traineeRepository, times(1)).findByUser_Username(credentials.userName());
        verify(userService, times(1)).changeActiveStatus(trainee.getUser().getId(), isActive);

        isActive = false;
        user.setIsActive(true);
        when(userService.changeActiveStatus(trainee.getUser().getId(), isActive)).thenReturn(true);
        assertTrue(traineeService.changeActiveStatus(isActive, credentials));
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(traineeRepository, times(2)).findByUser_Username(credentials.userName());
        verify(userService, times(1)).changeActiveStatus(trainee.getUser().getId(), isActive);
    }

    @Test
    void deleteTrainee() {
        Credentials credentials = new Credentials("username", "password");
        String userName = "testUser";

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertFalse(traineeService.deleteTrainee(userName, credentials));
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.of(new Trainee()));
        assertTrue(traineeService.deleteTrainee(userName, credentials));
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(traineeRepository, times(1)).findByUser_Username(userName);
        verify(traineeRepository, times(1)).deleteByUser_Username(userName);

        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.empty());
        assertFalse(traineeService.deleteTrainee(userName, credentials));
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(traineeRepository, times(2)).findByUser_Username(userName);
        verify(traineeRepository, times(1)).deleteByUser_Username(userName);
    }

    @Test
    void getTrainers() {
        Credentials credentials = new Credentials("username", "password");
        String userName = "testUser";

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertTrue(traineeService.getTrainers(userName, credentials).isEmpty());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.empty());
        assertTrue(traineeService.getTrainers(userName, credentials).isEmpty());
        verify(userService, times(2)).validateUserCredentials(credentials);


        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.of(new Trainee()));
        when(trainingService.getTrainersOfTrainee(userName)).thenReturn(Collections.singletonList(new Trainer()));
        assertFalse(traineeService.getTrainers(userName, credentials).isEmpty());
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(traineeRepository, times(2)).findByUser_Username(userName);
        verify(trainingService, times(1)).getTrainersOfTrainee(userName);
    }

    @Test
    void updateTrainersList() {
        Credentials credentials = new Credentials("username", "password");
        String userName = "testUser";
        Trainee trainee = new Trainee();
        trainee.setUser(new User() {{
            setUsername(userName);
        }});

        Trainer trainer = new Trainer();
        trainer.setUser(new User() {{
            setUsername("trainer");
        }});

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertTrue(traineeService.updateTrainersList(userName, Collections.emptyList(), credentials).isEmpty());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.empty());
        assertTrue(traineeService.updateTrainersList(userName, Collections.emptyList(), credentials).isEmpty());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(traineeRepository, times(1)).findByUser_Username(userName);

        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.of(trainee));
        assertTrue(traineeService.updateTrainersList(userName, Collections.emptyList(), credentials).isEmpty());
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(traineeRepository, times(2)).findByUser_Username(userName);

        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.of(trainee));
        when(trainingService.getTrainersOfTrainee(userName)).thenReturn(Collections.singletonList(trainer));
        assertFalse(traineeService.updateTrainersList(userName, Collections.singletonList("trainer"), credentials).isEmpty());
        verify(userService, times(4)).validateUserCredentials(credentials);
        verify(traineeRepository, times(3)).findByUser_Username(userName);
        verify(trainingService, times(3)).getTrainersOfTrainee(userName);
    }

    @Test
    public void getActiveTrainersNotAssignedTo() {
        Credentials credentials = new Credentials("username", "password");
        String userName = "testUser";
        Trainee trainee = new Trainee();
        trainee.setUser(new User() {{
            setUsername(userName);
        }});
        Trainer trainer = new Trainer();
        trainer.setUser(new User() {{
            setUsername("trainer");
            setIsActive(true);
        }});

        when(userService.validateUserCredentials(credentials)).thenReturn(true);
        assertTrue(traineeService.getActiveTrainersNotAssignedTo(userName, credentials).isEmpty());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.empty());
        assertTrue(traineeService.getActiveTrainersNotAssignedTo(userName, credentials).isEmpty());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(traineeRepository, times(1)).findByUser_Username(userName);

        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.of(trainee));
        when(trainingService.getTrainersOfTrainee(userName)).thenReturn(Collections.singletonList(trainer));
        when(trainerService.getAllTrainers(credentials)).thenReturn(Collections.singletonList(trainer));
        assertTrue(traineeService.getActiveTrainersNotAssignedTo(userName, credentials).isEmpty());
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(traineeRepository, times(2)).findByUser_Username(userName);
        verify(trainingService, times(1)).getTrainersOfTrainee(userName);
        verify(trainerService, times(1)).getAllTrainers(credentials);
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
        assertTrue(traineeService.getTrainings(userName, from, to, trainerUserName, trainingType.name(), credentials).isEmpty());
        verify(userService, times(1)).validateUserCredentials(credentials);

        when(userService.validateUserCredentials(credentials)).thenReturn(false);
        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.empty());
        assertTrue(traineeService.getTrainings(userName, from, to, trainerUserName, trainingType.name(), credentials).isEmpty());
        verify(userService, times(2)).validateUserCredentials(credentials);
        verify(traineeRepository, times(1)).findByUser_Username(userName);

        when(traineeRepository.findByUser_Username(userName)).thenReturn(Optional.of(new Trainee()));
        when(trainingService.getTraineeTrainingsList(userName, from, to, trainerUserName, trainingType)).thenReturn(Collections.emptyList());
        assertTrue(traineeService.getTrainings(userName, from, to, trainerUserName, trainingType.name(), credentials).isEmpty());
        verify(userService, times(3)).validateUserCredentials(credentials);
        verify(traineeRepository, times(2)).findByUser_Username(userName);
        verify(trainingService, times(1)).getTraineeTrainingsList(userName, from, to, trainerUserName, trainingType);

        when(trainingService.getTraineeTrainingsList(userName, from, to, trainerUserName, trainingType)).thenReturn(Collections.singletonList(null));
        assertFalse(traineeService.getTrainings(userName, from, to, trainerUserName, trainingType.name(), credentials).isEmpty());
        verify(userService, times(4)).validateUserCredentials(credentials);
        verify(traineeRepository, times(3)).findByUser_Username(userName);
        verify(trainingService, times(2)).getTraineeTrainingsList(userName, from, to, trainerUserName, trainingType);
    }
}
