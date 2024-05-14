package com.example.task_hibernate.service;

import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.TrainingType;
import com.example.task_hibernate.model.enums.ExerciseType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainingService {

    List<Training> getAllTrainings();

    List<TrainingType> getAllTrainingTypes();

    Optional<Training> getTrainingById(Long id);

    List<Training> getTraineeTrainingsList(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType);

    List<Training> getTrainerTrainingsList(String trainerUsername, Date fromDate, Date toDate, String traineeName);

    Boolean addTraining(String traineeUserName, String trainerUserName, String trainingName, Date trainingDate, int trainingDuration);

    List<Trainer> getTrainersOfTrainee(String traineeUsername);

    List<Trainee> getTraineesOfTrainer(String trainerUsername);

    Boolean deleteTrainingsWithTrainers(String traineeUsername, List<String> trainerUsernames);

}
