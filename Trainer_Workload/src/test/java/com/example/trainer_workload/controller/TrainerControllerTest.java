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
        trainer.setTrainerUsername("john_doe");
        trainer.setTrainerFirstName("John");
        trainer.setTrainerLastName("Doe");
        trainer.setActive(true);
    }

    @Test
    @WithMockUser
    void testHandleTrainerWorkloadAdd() throws Exception {
        when(trainerService.handleTrainerWorkload(anyString(), anyString(), anyString(), anyBoolean(), any(LocalDate.class), anyInt(), anyString()))
                .thenReturn(trainer);

        mockMvc.perform(post("/trainers/workload")
                        .param("username", "john_doe")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("isActive", "true")
                        .param("trainingDate", "2024-06-01")
                        .param("trainingDuration", "60")
                        .param("actionType", "ADD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("john_doe"))
                .andExpect(jsonPath("$.trainerFirstName").value("John"))
                .andExpect(jsonPath("$.trainerLastName").value("Doe"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser
    void testHandleTrainerWorkloadDelete() throws Exception {
        when(trainerService.handleTrainerWorkload(anyString(), anyString(), anyString(), anyBoolean(), any(LocalDate.class), anyInt(), anyString()))
                .thenReturn(trainer);

        mockMvc.perform(post("/trainers/workload")
                        .param("username", "john_doe")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("isActive", "true")
                        .param("trainingDate", "2024-06-01")
                        .param("trainingDuration", "60")
                        .param("actionType", "DELETE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("john_doe"))
                .andExpect(jsonPath("$.trainerFirstName").value("John"))
                .andExpect(jsonPath("$.trainerLastName").value("Doe"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser
    void testGetTrainerByUsername() throws Exception {
        when(trainerService.getTrainerByUsername(anyString())).thenReturn(Optional.of(trainer));

        mockMvc.perform(get("/trainers/john_doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("john_doe"))
                .andExpect(jsonPath("$.trainerFirstName").value("John"))
                .andExpect(jsonPath("$.trainerLastName").value("Doe"))
                .andExpect(jsonPath("$.active").value(true));
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
                        .content("{\"trainerUsername\": \"john_doe\", \"trainerFirstName\": \"John\", \"trainerLastName\": \"Doe\", \"isActive\": true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trainerUsername").value("john_doe"))
                .andExpect(jsonPath("$.trainerFirstName").value("John"))
                .andExpect(jsonPath("$.trainerLastName").value("Doe"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser
    void testDeleteTrainer() throws Exception {
        mockMvc.perform(delete("/trainers/john_doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
