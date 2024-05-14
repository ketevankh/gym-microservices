package com.example.trainer_workload.model.request;

import com.example.trainer_workload.model.enums.ActionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkloadRequest {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean isActive;
    private LocalDate trainingDate;
    private int trainingDuration;
    private ActionType actionType; // Enum for ADD/DELETE
}
