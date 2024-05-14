package com.example.task_hibernate.model.dto.serviceDTOs;

import com.example.task_hibernate.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerDTO {
    private TrainingType specialization;
    private UserDTO user;
}
