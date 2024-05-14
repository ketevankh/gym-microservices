package com.example.task_hibernate.service;

import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.dto.serviceDTOs.TraineeDTO;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public interface TraineeService {
    Trainee createTrainee(TraineeDTO traineeDTO);

    List<Trainee> getAllTrainees(Credentials credentials);

    Optional<Trainee> getTraineeById(Long id, Credentials credentials);

    Optional<Trainee> getTraineeByUsername(String userName, Credentials credentials);

    boolean changeTraineePassword(String password, Credentials credentials);

    Optional<Trainee> updateTrainee(TraineeDTO trainee, Credentials credentials);

    boolean changeActiveStatus(Boolean isActive, Credentials credentials);

    boolean deleteTrainee(String username, Credentials credentials);

    List<Training> getTrainings(String userName, Date from, Date to, String trainerUserName, String trainingType, Credentials credentials);

    List<Trainer> getTrainers(String userName, Credentials credentials);

    List<Trainer> getActiveTrainersNotAssignedTo(String userName, Credentials credentials);

    List<Trainer> updateTrainersList(String userName, List<String> trainerUsernames, Credentials credentials);
}
