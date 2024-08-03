package com.example.trainer_workload.controller;

import com.example.trainer_workload.security.SecurityConfigTest;
import com.example.trainer_workload.model.Trainer;
import com.example.trainer_workload.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
@Import(SecurityConfigTest.class)
public class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainer = new Trainer();
        trainer.setUsername("john_doe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setStatus(true);
    }

    @Test
    @WithMockUser
    void testHandleTrainerWorkloadAdd() throws Exception {
        when(trainerService.handleTrainerWorkload(anyString(), anyString(), anyString(), anyBoolean(), any(LocalDate.class), anyInt(), anyString()))
                .thenReturn(trainer);

        mockMvc.perform(post("/trainers/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainerUsername\": \"john_doe\", \"trainerFirstName\": \"John\", \"trainerLastName\": \"Doe\", \"isActive\": true, \"trainingDate\": \"2024-06-01\", \"trainingDuration\": 60, \"actionType\": \"ADD\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    @WithMockUser
    void testHandleTrainerWorkloadDelete() throws Exception {
        when(trainerService.handleTrainerWorkload(anyString(), anyString(), anyString(), anyBoolean(), any(LocalDate.class), anyInt(), anyString()))
                .thenReturn(trainer);

        mockMvc.perform(post("/trainers/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainerUsername\": \"john_doe\", \"trainerFirstName\": \"John\", \"trainerLastName\": \"Doe\", \"isActive\": true, \"trainingDate\": \"2024-06-01\", \"trainingDuration\": 60, \"actionType\": \"DELETE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    @WithMockUser
    void testGetTrainerByUsername() throws Exception {
        when(trainerService.getTrainerByUsername(anyString())).thenReturn(Optional.of(trainer));

        mockMvc.perform(get("/trainers/john_doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    @WithMockUser
    void testGetTrainerByUsernameNotFound() throws Exception {
        when(trainerService.getTrainerByUsername(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/trainers/john_doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testSaveTrainer() throws Exception {
        when(trainerService.saveTrainer(any(Trainer.class))).thenReturn(trainer);

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"john_doe\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"isActive\": true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    @WithMockUser
    void testDeleteTrainer() throws Exception {
        mockMvc.perform(delete("/trainers/john_doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
