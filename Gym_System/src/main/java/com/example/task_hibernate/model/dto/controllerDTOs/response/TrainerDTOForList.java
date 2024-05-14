package com.example.task_hibernate.model.dto.controllerDTOs.response;

import com.example.task_hibernate.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerDTOForList {
    private String UserName;
    private String firstName;
    private String lastName;
    private TrainingType specialization;
}
