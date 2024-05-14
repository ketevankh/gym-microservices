package com.example.task_hibernate.model.dto.controllerDTOs.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingTraineeResponseDTO {
    private String trainingName;
    private String trainingType;
    private Date trainingDate;
    private int duration;
    private String traineeUserName;
}
