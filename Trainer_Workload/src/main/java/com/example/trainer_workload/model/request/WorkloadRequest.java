package com.example.trainer_workload.model.request;

import com.example.trainer_workload.model.enums.ActionType;
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
    private ActionType actionType; // Enum for ADD/DELETE
}
