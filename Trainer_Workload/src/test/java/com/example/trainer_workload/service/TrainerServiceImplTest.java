package com.example.trainer_workload.service;

import com.example.trainer_workload.model.Trainer;
import com.example.trainer_workload.model.TrainingSummary;
import com.example.trainer_workload.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleTrainerWorkloadAdd() {
        String username = "john_doe";
        String firstName = "John";
        String lastName = "Doe";
        boolean isActive = true;
        LocalDate trainingDate = LocalDate.of(2024, 6, 7);
        int trainingDuration = 60;
        String actionType = "ADD";

        Trainer existingTrainer = new Trainer();
        existingTrainer.setTrainerUsername(username);
        Trainer savedTrainer = new Trainer();
        savedTrainer.setTrainerUsername(username);

        when(trainerRepository.findById(username)).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(savedTrainer);

        Trainer result = trainerService.handleTrainerWorkload(username, firstName, lastName, isActive, trainingDate, trainingDuration, actionType);

        ArgumentCaptor<Trainer> trainerArgumentCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository, times(1)).save(trainerArgumentCaptor.capture());
        Trainer capturedTrainer = trainerArgumentCaptor.getValue();

        assertNotNull(result);
        assertEquals(username, capturedTrainer.getTrainerUsername());
        assertEquals(firstName, capturedTrainer.getTrainerFirstName());
        assertEquals(lastName, capturedTrainer.getTrainerLastName());
        assertEquals(isActive, capturedTrainer.isActive());
        assertEquals(1, capturedTrainer.getTrainingSummary().size());

        TrainingSummary savedTrainingSummary = capturedTrainer.getTrainingSummary().get(0);
        assertEquals(trainingDate.getYear(), savedTrainingSummary.getTrainingYear());
        assertEquals(trainingDate.getMonthValue(), savedTrainingSummary.getTrainingMonth());
        assertEquals(trainingDuration, savedTrainingSummary.getTrainingDuration());
    }

    @Test
    void testHandleTrainerWorkloadDelete() {
        String username = "john_doe";
        String firstName = "John";
        String lastName = "Doe";
        boolean isActive = true;
        LocalDate trainingDate = LocalDate.of(2024, 6, 7);
        int trainingDuration = 60;
        String actionType = "DELETE";

        Trainer existingTrainer = new Trainer();
        existingTrainer.setTrainerUsername(username);
        TrainingSummary trainingSummary = new TrainingSummary();
        trainingSummary.setTrainingYear(trainingDate.getYear());
        trainingSummary.setTrainingMonth(trainingDate.getMonthValue());
        trainingSummary.setTrainingDuration(trainingDuration);
        existingTrainer.getTrainingSummary().add(trainingSummary);
        Trainer savedTrainer = new Trainer();
        savedTrainer.setTrainerUsername(username);

        when(trainerRepository.findById(username)).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(savedTrainer);

        Trainer result = trainerService.handleTrainerWorkload(username, firstName, lastName, isActive, trainingDate, trainingDuration, actionType);

        ArgumentCaptor<Trainer> trainerArgumentCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository, times(1)).save(trainerArgumentCaptor.capture());
        Trainer capturedTrainer = trainerArgumentCaptor.getValue();

        assertNotNull(result);
        assertEquals(username, capturedTrainer.getTrainerUsername());
        assertEquals(firstName, capturedTrainer.getTrainerFirstName());
        assertEquals(lastName, capturedTrainer.getTrainerLastName());
        assertEquals(isActive, capturedTrainer.isActive());
        assertEquals(0, capturedTrainer.getTrainingSummary().size());
    }

    @Test
    void testHandleTrainerWorkloadAddNewTrainer() {
        String username = "jane_doe";
        String firstName = "Jane";
        String lastName = "Doe";
        boolean isActive = true;
        LocalDate trainingDate = LocalDate.of(2024, 6, 7);
        int trainingDuration = 60;
        String actionType = "ADD";

        Trainer newTrainer = new Trainer();
        newTrainer.setTrainerUsername(username);

        when(trainerRepository.findById(username)).thenReturn(Optional.empty());
        when(trainerRepository.save(any(Trainer.class))).thenReturn(newTrainer);

        Trainer result = trainerService.handleTrainerWorkload(username, firstName, lastName, isActive, trainingDate, trainingDuration, actionType);

        ArgumentCaptor<Trainer> trainerArgumentCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository, times(1)).save(trainerArgumentCaptor.capture());
        Trainer capturedTrainer = trainerArgumentCaptor.getValue();

        assertNotNull(result);
        assertEquals(username, capturedTrainer.getTrainerUsername());
        assertEquals(firstName, capturedTrainer.getTrainerFirstName());
        assertEquals(lastName, capturedTrainer.getTrainerLastName());
        assertEquals(isActive, capturedTrainer.isActive());
        assertEquals(1, capturedTrainer.getTrainingSummary().size());

        TrainingSummary savedTrainingSummary = capturedTrainer.getTrainingSummary().get(0);
        assertEquals(trainingDate.getYear(), savedTrainingSummary.getTrainingYear());
        assertEquals(trainingDate.getMonthValue(), savedTrainingSummary.getTrainingMonth());
        assertEquals(trainingDuration, savedTrainingSummary.getTrainingDuration());
    }

    @Test
    void testHandleTrainerWorkloadInvalidAction() {
        String username = "john_doe";
        String firstName = "John";
        String lastName = "Doe";
        boolean isActive = true;
        LocalDate trainingDate = LocalDate.of(2024, 6, 7);
        int trainingDuration = 60;
        String actionType = "INVALID_ACTION";

        Trainer existingTrainer = new Trainer();
        existingTrainer.setTrainerUsername(username);

        when(trainerRepository.findById(username)).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        Trainer result = trainerService.handleTrainerWorkload(username, firstName, lastName, isActive, trainingDate, trainingDuration, actionType);

        ArgumentCaptor<Trainer> trainerArgumentCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository, times(1)).save(trainerArgumentCaptor.capture());
        Trainer capturedTrainer = trainerArgumentCaptor.getValue();

        assertNotNull(result);
        assertEquals(username, capturedTrainer.getTrainerUsername());
        assertEquals(firstName, capturedTrainer.getTrainerFirstName());
        assertEquals(lastName, capturedTrainer.getTrainerLastName());
        assertEquals(isActive, capturedTrainer.isActive());
        assertEquals(0, capturedTrainer.getTrainingSummary().size());
    }
}
