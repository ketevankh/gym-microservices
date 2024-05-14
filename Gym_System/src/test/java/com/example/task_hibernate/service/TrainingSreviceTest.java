package com.example.task_hibernate.service;

import com.example.task_hibernate.model.*;
import com.example.task_hibernate.model.enums.ExerciseType;
import com.example.task_hibernate.repository.TrainingRepository;
import com.example.task_hibernate.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    @Mock
    TrainingRepository trainingRepository;

    @InjectMocks
    TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTrainings() {
        trainingService.getAllTrainings();
        verify(trainingRepository, times(1)).findAll();
    }

    @Test
    void getTrainingById() {
        Long id = 1L;
        trainingService.getTrainingById(id);
        verify(trainingRepository, times(1)).findById(id);
    }

    @Test
    void getTraineeTrainingsList() {
        String traineeUsername = "test";
        Date fromDate = new Date();
        Date toDate = new Date();
        String trainerName = "test";
        ExerciseType trainingType = ExerciseType.CARDIO;
        trainingService.getTraineeTrainingsList(traineeUsername, fromDate, toDate, trainerName, trainingType);
        verify(trainingRepository, times(1)).findByTraineeUsernameAndCriteria(traineeUsername, fromDate, toDate, trainerName, trainingType);
    }

    @Test
    void getTrainerTrainingsList() {
        String trainerUsername = "test";
        Date fromDate = new Date();
        Date toDate = new Date();
        String traineeName = "test";
        trainingService.getTrainerTrainingsList(trainerUsername, fromDate, toDate, traineeName);
        verify(trainingRepository, times(1)).findByTrainerUsernameAndCriteria(trainerUsername, fromDate, toDate, traineeName);
    }

    @Test
    void getTrainersOfTrainee() {
        String traineeUsername = "test";
        trainingService.getTrainersOfTrainee(traineeUsername);
        verify(trainingRepository, times(1)).findTrainerByTraineeUsername(traineeUsername);
    }

    @Test
    void createTraining() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("traineeUsername");
        trainee.setUser(user);
        trainee.setId(1L);


        Trainer trainer = new Trainer();
        User userTrainer = new User();
        userTrainer.setUsername("trainerUsername");
        trainer.setUser(userTrainer);
        trainer.setId(1L);

        TrainingType trainingType = new TrainingType(1L, ExerciseType.CARDIO);
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingDate(new Date());

        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        Training createdTraining = trainingService.createTraining(training).get();

        verify(trainingRepository, times(1)).save(any(Training.class));

        assertNotNull(createdTraining);
        assertEquals(trainee.getUser().getUsername(), createdTraining.getTrainee().getUser().getUsername());
        assertEquals(trainer.getUser().getUsername(), createdTraining.getTrainer().getUser().getUsername());
        assertEquals(trainingType.getTrainingType(), createdTraining.getTrainingType().getTrainingType());
    }
}