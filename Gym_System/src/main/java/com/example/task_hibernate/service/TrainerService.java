package com.example.task_hibernate.service;

import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.serviceDTOs.TrainerDTO;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer createTrainer(TrainerDTO trainer);

    List<Trainer> getAllTrainers(Credentials credentials);

    Optional<Trainer> getTrainerById(Long id, Credentials credentials);

    Optional<Trainer> getTrainerByUserName(String userName, Credentials credentials);

    List<Training> getTrainings(String userName, Date from, Date to, String trainerUserName, String trainingType, Credentials credentials);

    boolean changeTrainerPassword(String password, Credentials credentials);

    Optional<Trainer> updateTrainer(TrainerDTO trainer, Credentials credentials);

    boolean changeActiveStatus(Boolean isActive, Credentials credentials);
    boolean deleteTrainer(String username, Credentials credentials);

    List<Trainee> getTrainees(String userName, Credentials credentials);

}
