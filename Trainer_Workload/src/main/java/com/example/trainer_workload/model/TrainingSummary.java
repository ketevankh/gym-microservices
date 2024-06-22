package com.example.trainer_workload.model;

import jakarta.persistence.Embeddable;
import lombok.Data;


@Data
@Embeddable
public class TrainingSummary {
    private int trainingYear;
    private int trainingMonth;
    private int trainingDuration;
}
