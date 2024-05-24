package com.example.trainer_workload.controller;

import com.example.trainer_workload.model.request.WorkloadRequest;
import com.example.trainer_workload.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkloadControllerTest {

    @InjectMocks
    private WorkloadController workloadController;

    @Mock
    private WorkloadService workloadService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateWorkload() throws Exception {
        WorkloadRequest request = new WorkloadRequest();
        ResponseEntity<String> response = workloadController.updateWorkload(request);

        verify(workloadService, times(1)).updateWorkload(request);
        assertEquals("Workload updated successfully.", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}