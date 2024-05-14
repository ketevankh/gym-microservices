package com.example.task_hibernate.model.dto.controllerDTOs.request;
import com.example.task_hibernate.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerRequestDTO {
    private String firstName;
    private String lastName;
    private String userName;
    private Boolean isActive;
    private TrainingType specialization;
}
