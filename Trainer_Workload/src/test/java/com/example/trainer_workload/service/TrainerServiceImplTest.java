package com.example.trainer_workload.service;

import com.example.trainer_workload.model.Trainer;
import com.example.trainer_workload.model.YearSummary;
import com.example.trainer_workload.model.MonthSummary;
import com.example.trainer_workload.model.WorkloadRequest;
import com.example.trainer_workload.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Captor
    private ArgumentCaptor<Trainer> trainerCaptor;

    private static final String DLQ_NAME = "workload.dlq";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(trainerService, "DLQ", DLQ_NAME);
    }

    @Test
    void getTrainerByUsername_existingTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.getTrainerByUsername("trainer1");

        assertTrue(result.isPresent());
        assertEquals("trainer1", result.get().getUsername());
    }

    @Test
    void getTrainerByUsername_nonExistingTrainer() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.getTrainerByUsername("trainer1");

        assertFalse(result.isPresent());
    }

    @Test
    void saveTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer result = trainerService.saveTrainer(trainer);

        assertNotNull(result);
        assertEquals("trainer1", result.getUsername());
    }

    @Test
    void deleteTrainer_existingTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        doNothing().when(trainerRepository).deleteById("trainer1");

        trainerService.deleteTrainer("trainer1");

        verify(trainerRepository, times(1)).deleteById("trainer1");
    }

    @Test
    void deleteTrainer_nonExistingTrainer() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.empty());

        trainerService.deleteTrainer("trainer1");

        verify(trainerRepository, never()).deleteById(anyString());
    }

    @Test
    void handleTrainerWorkload_addTrainingSummary_newTrainer() {
        LocalDate trainingDate = LocalDate.of(2023, 7, 10);

        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.empty());
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.handleTrainerWorkload("trainer1", "John", "Doe", true, trainingDate, 2, "ADD");

        verify(trainerRepository, times(1)).save(trainerCaptor.capture());
        Trainer capturedTrainer = trainerCaptor.getValue();

        assertEquals("trainer1", capturedTrainer.getUsername());
        assertEquals(1, capturedTrainer.getYears().size());
        assertEquals(1, capturedTrainer.getYears().get(0).getMonths().size());
        assertEquals(2, capturedTrainer.getYears().get(0).getMonths().get(0).getTrainingsSummaryDuration(), 0.001);
    }

    @Test
    void handleTrainerWorkload_addTrainingSummary_existingTrainer() {
        LocalDate trainingDate = LocalDate.of(2023, 7, 10);
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.handleTrainerWorkload("trainer1", "John", "Doe", true, trainingDate, 2, "ADD");

        verify(trainerRepository, times(1)).save(trainerCaptor.capture());
        Trainer capturedTrainer = trainerCaptor.getValue();

        assertEquals("trainer1", capturedTrainer.getUsername());
        assertEquals(1, capturedTrainer.getYears().size());
        assertEquals(1, capturedTrainer.getYears().get(0).getMonths().size());
        assertEquals(2, capturedTrainer.getYears().get(0).getMonths().get(0).getTrainingsSummaryDuration(), 0.001);
    }

    @Test
    void handleTrainerWorkload_deleteTrainingSummary_existingTrainer() {
        LocalDate trainingDate = LocalDate.of(2023, 7, 10);
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        YearSummary yearSummary = new YearSummary();
        yearSummary.setYear(2023);

        MonthSummary monthSummary = new MonthSummary();
        monthSummary.setMonth(7);
        monthSummary.setTrainingsSummaryDuration(2);

        yearSummary.setMonths(List.of(monthSummary));
        trainer.setYears(List.of(yearSummary));

        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.handleTrainerWorkload("trainer1", "John", "Doe", true, trainingDate, 2, "DELETE");

        verify(trainerRepository, times(1)).save(trainerCaptor.capture());
        Trainer capturedTrainer = trainerCaptor.getValue();

        assertEquals("trainer1", capturedTrainer.getUsername());
        assertTrue(capturedTrainer.getYears().isEmpty() || capturedTrainer.getYears().get(0).getMonths().isEmpty());
    }

    @Test
    void handleTrainerWorkload_sendToDLQOnError() {
        LocalDate trainingDate = LocalDate.of(2023, 7, 10);

        when(trainerRepository.findByUsername("trainer1")).thenThrow(new RuntimeException("Test exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            trainerService.handleTrainerWorkload("trainer1", "John", "Doe", true, trainingDate, 2, "ADD");
        });

        verify(jmsTemplate, times(1)).convertAndSend(eq(DLQ_NAME), any(WorkloadRequest.class));
        assertEquals("Test exception", exception.getMessage());
    }

    @Test
    void handleTrainerWorkload_invalidActionType() {
        LocalDate trainingDate = LocalDate.of(2023, 7, 10);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.handleTrainerWorkload("trainer1", "John", "Doe", true, trainingDate, 2, "INVALID_ACTION");
        });

        assertEquals("Invalid action type: INVALID_ACTION", exception.getMessage());
        verify(trainerRepository, never()).save(any(Trainer.class));
        verify(jmsTemplate, never()).convertAndSend(anyString(), any(WorkloadRequest.class));
    }
}
