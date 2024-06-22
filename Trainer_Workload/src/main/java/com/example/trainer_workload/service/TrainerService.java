package com.example.trainer_workload.service;

import com.example.trainer_workload.model.Trainer;

import java.time.LocalDate;
import java.util.Optional;

public interface TrainerService {
    Optional<Trainer> getTrainerByUsername(String username);
    Trainer saveTrainer(Trainer trainer);
    void deleteTrainer(String username);
    Trainer handleTrainerWorkload(String username, String firstName, String lastName, boolean isActive, LocalDate trainingDate, int trainingDuration, String actionType);
}
