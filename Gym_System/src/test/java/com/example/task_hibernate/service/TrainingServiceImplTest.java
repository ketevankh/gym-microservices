package com.example.task_hibernate.service;

import com.example.task_hibernate.exceptions.ResourceNotFoundException;
import com.example.task_hibernate.model.*;
import com.example.task_hibernate.model.dto.controllerDTOs.request.WorkloadRequest;
import com.example.task_hibernate.model.enums.ActionType;
import com.example.task_hibernate.model.enums.ExerciseType;
import com.example.task_hibernate.repository.TraineeRepository;
import com.example.task_hibernate.repository.TrainerRepository;
import com.example.task_hibernate.repository.TrainingRepository;
import com.example.task_hibernate.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User traineeUser = new User();
        traineeUser.setUsername("traineeUser");
        traineeUser.setFirstName("Trainee");
        traineeUser.setLastName("User");
        traineeUser.setIsActive(true);

        trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setUsername("trainerUser");
        trainerUser.setFirstName("Trainer");
        trainerUser.setLastName("User");
        trainerUser.setIsActive(true);

        trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setTrainingType(new TrainingType());

        training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Training Session");
        training.setTrainingDate(new Date());
        training.setDuration(60);
        training.setTrainingType(new TrainingType());
    }

    @Test
    void getAllTrainings() {
        when(trainingRepository.findAll()).thenReturn(Collections.singletonList(training));

        List<Training> trainings = trainingService.getAllTrainings();

        assertNotNull(trainings);
        assertEquals(1, trainings.size());
        verify(trainingRepository, times(1)).findAll();
    }

    @Test
    void getTrainingById() {
        Long id = 1L;
        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));

        Optional<Training> foundTraining = trainingService.getTrainingById(id);

        assertTrue(foundTraining.isPresent());
        assertEquals(training, foundTraining.get());
    }

    @Test
    void getTraineeTrainingsList() {
        String traineeUsername = "traineeUser";
        Date fromDate = new Date();
        Date toDate = new Date();
        String trainerName = "Trainer";
        String trainingType = "STRENGTH";

        when(trainingRepository.findByTraineeUsernameAndCriteria(anyString(), any(Date.class), any(Date.class), anyString(), any())).thenReturn(Collections.singletonList(training));

        List<Training> trainings = trainingService.getTraineeTrainingsList(traineeUsername, fromDate, toDate, trainerName, trainingType);

        assertNotNull(trainings);
        assertEquals(1, trainings.size());
        verify(trainingRepository, times(1)).findByTraineeUsernameAndCriteria(traineeUsername, fromDate, toDate, trainerName, ExerciseType.valueOf(trainingType));
    }

    @Test
    void getTrainerTrainingsList() {
        String trainerUsername = "trainerUser";
        Date fromDate = new Date();
        Date toDate = new Date();
        String traineeName = "Trainee";

        when(trainingRepository.findByTrainerUsernameAndCriteria(anyString(), any(Date.class), any(Date.class), anyString())).thenReturn(Collections.singletonList(training));

        List<Training> trainings = trainingService.getTrainerTrainingsList(trainerUsername, fromDate, toDate, traineeName);

        assertNotNull(trainings);
        assertEquals(1, trainings.size());
        verify(trainingRepository, times(1)).findByTrainerUsernameAndCriteria(trainerUsername, fromDate, toDate, traineeName);
    }

    @Test
    void addTraining() {
        String traineeUserName = "traineeUser";
        String trainerUserName = "trainerUser";
        String trainingName = "Training Session";
        Date trainingDate = new Date();
        int trainingDuration = 60;

        when(traineeRepository.findByUser_Username(traineeUserName)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUser_Username(trainerUserName)).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        Boolean result = trainingService.addTraining(traineeUserName, trainerUserName, trainingName, trainingDate, trainingDuration);

        assertTrue(result);
        verify(traineeRepository, times(1)).findByUser_Username(traineeUserName);
        verify(trainerRepository, times(1)).findByUser_Username(trainerUserName);
        verify(trainingRepository, times(1)).save(any(Training.class));
    }

    @Test
    void addTraining_TraineeNotFound() {
        String traineeUserName = "nonExistentUser";
        String trainerUserName = "trainerUser";
        String trainingName = "Training Session";
        Date trainingDate = new Date();
        int trainingDuration = 60;

        when(traineeRepository.findByUser_Username(traineeUserName)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.addTraining(traineeUserName, trainerUserName, trainingName, trainingDate, trainingDuration));
        verify(traineeRepository, times(1)).findByUser_Username(traineeUserName);
    }

    @Test
    void addTraining_TrainerNotFound() {
        String traineeUserName = "traineeUser";
        String trainerUserName = "nonExistentUser";
        String trainingName = "Training Session";
        Date trainingDate = new Date();
        int trainingDuration = 60;

        when(traineeRepository.findByUser_Username(traineeUserName)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUser_Username(trainerUserName)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.addTraining(traineeUserName, trainerUserName, trainingName, trainingDate, trainingDuration));
        verify(trainerRepository, times(1)).findByUser_Username(trainerUserName);
    }

    @Test
    void getTrainersOfTrainee() {
        String traineeUsername = "traineeUser";
        when(trainingRepository.findTrainerByTraineeUsername(traineeUsername)).thenReturn(Collections.singletonList(trainer));

        List<Trainer> trainers = trainingService.getTrainersOfTrainee(traineeUsername);

        assertNotNull(trainers);
        assertEquals(1, trainers.size());
        verify(trainingRepository, times(1)).findTrainerByTraineeUsername(traineeUsername);
    }

    @Test
    void getTraineesOfTrainer() {
        String trainerUsername = "trainerUser";
        when(trainingRepository.findTraineeByTrainerUsername(trainerUsername)).thenReturn(Collections.singletonList(trainee));

        List<Trainee> trainees = trainingService.getTraineesOfTrainer(trainerUsername);

        assertNotNull(trainees);
        assertEquals(1, trainees.size());
        verify(trainingRepository, times(1)).findTraineeByTrainerUsername(trainerUsername);
    }

    @Test
    void deleteTrainingsWithTrainers() {
        String traineeUsername = "traineeUser";
        List<String> trainerUsernames = Arrays.asList("trainerUser");

        doNothing().when(trainingRepository).deleteByTraineeUserUserNameAndTrainerUserUserNameIn(anyString(), anyList());
        when(trainerRepository.findByUser_Username(anyString())).thenReturn(Optional.of(trainer));

        Boolean result = trainingService.deleteTrainingsWithTrainers(traineeUsername, trainerUsernames);

        assertTrue(result);
        verify(trainingRepository, times(1)).deleteByTraineeUserUserNameAndTrainerUserUserNameIn(anyString(), anyList());
        verify(trainerRepository, times(1)).findByUser_Username(anyString());
    }

    @Test
    void getAllTrainingTypes() {
        when(trainingRepository.findAllTrainingTypes()).thenReturn(Collections.singletonList(new TrainingType()));

        List<TrainingType> trainingTypes = trainingService.getAllTrainingTypes();

        assertNotNull(trainingTypes);
        assertEquals(1, trainingTypes.size());
        verify(trainingRepository, times(1)).findAllTrainingTypes();
    }
    @Test
    void sendWorkloadUpdate() {
        // Mock the necessary dependencies and inputs
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("trainerUser");
        user.setFirstName("Trainer");
        user.setLastName("User");
        user.setIsActive(true);
        trainer.setUser(user);
        Date trainingDate = new Date();
        int trainingDuration = 60;
        ActionType actionType = ActionType.ADD;

        WorkloadRequest request = new WorkloadRequest();
        request.setTrainerUsername(trainer.getUser().getUsername());
        request.setTrainerFirstName(trainer.getUser().getFirstName());
        request.setTrainerLastName(trainer.getUser().getLastName());
        request.setIsActive(trainer.getUser().getIsActive());
        request.setTrainingDate(trainingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        request.setTrainingDuration(trainingDuration);
        request.setActionType(actionType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WorkloadRequest> entity = new HttpEntity<>(request, headers);

        // Mock the RestTemplate behavior
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class))).thenReturn(ResponseEntity.ok("Success"));

        // Invoke the method
        trainingService.sendWorkloadUpdate(trainer, trainingDate, trainingDuration, actionType);

        // Verify interactions and behavior
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }
    @Test
    void sendWorkloadUpdate_ExceptionThrown() {
        // Mock the necessary dependencies and inputs
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("trainerUser");
        user.setFirstName("Trainer");
        user.setLastName("User");
        user.setIsActive(true);
        trainer.setUser(user);
        Date trainingDate = new Date();
        int trainingDuration = 60;
        ActionType actionType = ActionType.ADD;

        WorkloadRequest request = new WorkloadRequest();
        request.setTrainerUsername(trainer.getUser().getUsername());
        request.setTrainerFirstName(trainer.getUser().getFirstName());
        request.setTrainerLastName(trainer.getUser().getLastName());
        request.setIsActive(trainer.getUser().getIsActive());
        request.setTrainingDate(trainingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        request.setTrainingDuration(trainingDuration);
        request.setActionType(actionType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WorkloadRequest> entity = new HttpEntity<>(request, headers);

        // Mock the RestTemplate behavior to throw an exception
        doThrow(new RestClientException("Error occurred")).when(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));

        // Invoke the method and handle the exception
        try {
            trainingService.sendWorkloadUpdate(trainer, trainingDate, trainingDuration, actionType);
        } catch (RestClientException e) {
            // Expected exception, do nothing
        }

        // Verify that the fallback method was called
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }




    @Test
    void fallbackSendWorkloadUpdate() {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("trainerUser");
        trainer.setUser(user);
        Date trainingDate = new Date();
        int trainingDuration = 60;
        ActionType actionType = ActionType.ADD;
        Throwable throwable = new Throwable("Error");

        trainingService.fallbackSendWorkloadUpdate(trainer, trainingDate, trainingDuration, actionType, throwable);

        // Since the fallback method logs the error, we verify that it completes without exceptions
        assertDoesNotThrow(() -> trainingService.fallbackSendWorkloadUpdate(trainer, trainingDate, trainingDuration, actionType, throwable));
    }
}
