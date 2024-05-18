package com.example.trainer_workload.model;

import lombok.Data;

import java.util.Date;

@Data
public class TrainingSummary {
    private String trainingName;
    private Date trainingDate;
    private int duration;
}