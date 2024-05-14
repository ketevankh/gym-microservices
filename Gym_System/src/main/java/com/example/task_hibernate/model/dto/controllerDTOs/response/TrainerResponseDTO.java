package com.example.task_hibernate.model.dto.controllerDTOs.response;

import com.example.task_hibernate.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerResponseDTO {
    private String firstName;
    private String lastName;
    private TrainingType specialization;
    private Boolean isActive;
    private List<TraineeDTOForList> traineesList;
}
