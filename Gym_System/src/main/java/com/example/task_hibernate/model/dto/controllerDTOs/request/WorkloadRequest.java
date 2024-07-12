package com.example.task_hibernate.model.dto.controllerDTOs.request;

import com.example.task_hibernate.model.enums.ActionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class WorkloadRequest {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    private LocalDateTime trainingDate;
    private int trainingDuration;
    private String actionType;
}