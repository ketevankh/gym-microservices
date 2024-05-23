package com.example.trainer_workload.service;

import com.example.trainer_workload.model.MonthlySummary;
import com.example.trainer_workload.model.enums.ActionType;
import com.example.trainer_workload.model.request.WorkloadRequest;
import com.example.trainer_workload.service.impl.WorkloadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceImplTest {

    @InjectMocks
    private WorkloadServiceImpl workloadService;

    @InjectMocks
    private WorkloadRequest workloadRequest;

    @BeforeEach
    void resetWorkloadService() {
        workloadService.trainersWorkload.clear();
    }

    @BeforeEach
    void setUp() {
        workloadRequest = new WorkloadRequest();
        workloadRequest.setTrainerUsername("john_doe");
        workloadRequest.setTrainerFirstName("John");
        workloadRequest.setTrainerLastName("Doe");
        workloadRequest.setIsActive(true);
        workloadRequest.setTrainingDate(LocalDateTime.of(2023, 5, 15, 0, 0));
    }

    @Test
    void testAddWorkload() throws Exception {
        workloadRequest.setActionType(ActionType.ADD);
        workloadRequest.setTrainingDuration(120);

        workloadService.updateWorkload(workloadRequest);

        MonthlySummary summary = workloadService.trainersWorkload.get("john_doe");
        assertNotNull(summary);
        assertEquals(120, (int) summary.getYears().get(2023).get(5));
    }

    @Test
    void testDeleteWorkload() throws Exception {
        // First add some workload
        workloadRequest.setActionType(ActionType.ADD);
        workloadRequest.setTrainingDuration(120);
        workloadService.updateWorkload(workloadRequest);

        // Now delete the workload
        workloadRequest.setActionType(ActionType.DELETE);
        workloadRequest.setTrainingDuration(60);
        workloadService.updateWorkload(workloadRequest);

        MonthlySummary summary = workloadService.trainersWorkload.get("john_doe");
        assertNotNull(summary);
        assertEquals(60, (int) summary.getYears().get(2023).get(5));
    }

    @Test
    void testDeleteWorkloadNoExistingData() {
        workloadRequest.setActionType(ActionType.DELETE);
        workloadRequest.setTrainingDuration(120);

        Exception exception = assertThrows(Exception.class, () -> {
            workloadService.updateWorkload(workloadRequest);
        });

        String expectedMessage = "Trainer does not have any training in the given year to delete";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDecrementWorkloadWithoutYearData() throws Exception {
        workloadRequest.setActionType(ActionType.ADD);
        workloadRequest.setTrainingDuration(120);
        workloadService.updateWorkload(workloadRequest);

        workloadRequest.setActionType(ActionType.DELETE);
        workloadRequest.setTrainingDuration(60);
        workloadService.updateWorkload(workloadRequest);

        workloadRequest.setTrainingDuration(100);
        Exception exception = assertThrows(Exception.class, () -> {
            workloadService.updateWorkload(workloadRequest);
        });

        String expectedMessage = "Trainer does not have enough training duration in the given month to delete";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDecrementWorkloadWithoutMonthData() throws Exception {
        workloadRequest.setActionType(ActionType.ADD);
        workloadRequest.setTrainingDuration(120);
        workloadService.updateWorkload(workloadRequest);

        workloadRequest.setActionType(ActionType.DELETE);
        workloadRequest.setTrainingDuration(60);
        workloadService.updateWorkload(workloadRequest);

        workloadService.trainersWorkload.get("john_doe").getYears().get(2023).remove(5);
        Exception exception = assertThrows(Exception.class, () -> {
            workloadService.updateWorkload(workloadRequest);
        });

        String expectedMessage = "Trainer does not have any training in the given month to delete";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDecrementWorkloadWithInsufficientDuration() throws Exception {
        workloadRequest.setActionType(ActionType.ADD);
        workloadRequest.setTrainingDuration(120);
        workloadService.updateWorkload(workloadRequest);

        workloadRequest.setActionType(ActionType.DELETE);
        workloadRequest.setTrainingDuration(60);
        workloadService.updateWorkload(workloadRequest);

        workloadRequest.setTrainingDuration(100);
        Exception exception = assertThrows(Exception.class, () -> {
            workloadService.updateWorkload(workloadRequest);
        });

        String expectedMessage = "Trainer does not have enough training duration in the given month to delete";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
