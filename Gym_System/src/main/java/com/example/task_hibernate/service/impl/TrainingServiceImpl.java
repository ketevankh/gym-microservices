package com.example.task_hibernate.service.impl;

import com.example.task_hibernate.exceptions.ResourceNotFoundException;
import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.TrainingType;
import com.example.task_hibernate.model.enums.ExerciseType;
import com.example.task_hibernate.repository.TraineeRepository;
import com.example.task_hibernate.repository.TrainerRepository;
import com.example.task_hibernate.repository.TrainingRepository;
import com.example.task_hibernate.service.TrainingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    @Override
    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    @Override
    public Optional<Training> getTrainingById(Long id) {
        return trainingRepository.findById(id);
    }

    @Override
    public List<Training> getTraineeTrainingsList(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType) {
        return trainingRepository.findByTraineeUsernameAndCriteria(traineeUsername, fromDate, toDate, trainerName, trainingType != null ? ExerciseType.valueOf(trainingType) : null);
    }

    @Override
    public List<Training> getTrainerTrainingsList(String trainerUsername, Date fromDate, Date toDate, String traineeName) {
        return trainingRepository.findByTrainerUsernameAndCriteria(trainerUsername, fromDate, toDate, traineeName);
    }

    @Override
    public Boolean addTraining(String traineeUserName, String trainerUserName, String trainingName, Date trainingDate, int trainingDuration) {
        Optional<Trainee> trainee = traineeRepository.findByUser_Username(traineeUserName);
        if (trainee.isEmpty()) {
            log.error("Trainee with username {} not found", traineeUserName);
            throw new ResourceNotFoundException("Trainee with username " + traineeUserName + " not found");
        }

        Optional<Trainer> trainer = trainerRepository.findByUser_Username(trainerUserName);
        if (trainer.isEmpty()) {
            log.error("Trainer with username {} not found", trainerUserName);
            throw new ResourceNotFoundException("Trainer with username " + trainerUserName + " not found");
        }

        Training training = new Training();
        training.setTrainee(trainee.get());
        training.setTrainer(trainer.get());
        training.setTrainingName(trainingName);
        training.setTrainingDate(trainingDate);
        training.setDuration(trainingDuration);
        training.setTrainingType(trainer.get().getTrainingType());
        trainingRepository.save(training);
        return true;
    }

    @Override
    public List<Trainer> getTrainersOfTrainee(String traineeUsername) {
        return trainingRepository.findTrainerByTraineeUsername(traineeUsername);
    }

    @Override
    public List<Trainee> getTraineesOfTrainer(String trainerUsername) {
        return trainingRepository.findTraineeByTrainerUsername(trainerUsername);
    }

    @Override
    public Boolean deleteTrainingsWithTrainers(String traineeUsername, List<String> trainerUsernames) {
        trainingRepository.deleteByTraineeUserUserNameAndTrainerUserUserNameIn(traineeUsername, trainerUsernames);
        return true;
    }

    @Override
    public List<TrainingType> getAllTrainingTypes() {
        return trainingRepository.findAllTrainingTypes();
    }
}
