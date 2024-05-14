package com.example.task_hibernate.service.impl;

import com.example.task_hibernate.exceptions.ResourceNotFoundException;
import com.example.task_hibernate.exceptions.UpdateFailedException;
import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.User;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.serviceDTOs.TraineeDTO;
import com.example.task_hibernate.repository.TraineeRepository;
import com.example.task_hibernate.service.TraineeService;
import com.example.task_hibernate.service.TrainerService;
import com.example.task_hibernate.service.TrainingService;
import com.example.task_hibernate.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;

    private final UserService userService;
    private final TrainingService trainingService;
    private final TrainerService trainerService;

    @Override
    public Trainee createTrainee(TraineeDTO traineeDTO) {
        User user = userService.createUser(traineeDTO.getUser());

        Trainee trainee = new Trainee();
        trainee.setAddress(traineeDTO.getAddress());
        trainee.setDateOfBirth(traineeDTO.getDateOfBirth());
        trainee.setUser(user);

        return traineeRepository.save(trainee);
    }

    @Override
    public List<Trainee> getAllTrainees(Credentials credentials) {
        userService.validateUserCredentials(credentials);
        return traineeRepository.findAll();
    }

    @Override
    public Optional<Trainee> getTraineeById(Long id, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        return traineeRepository.findById(id);
    }

    @Override
    public Optional<Trainee> getTraineeByUsername(String userName, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        return traineeRepository.findByUser_Username(userName);
    }

    @Override
    public boolean changeTraineePassword(String password, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        return userService.changeUserPassword(credentials.userName(), password);
    }

    @Override
    public Optional<Trainee> updateTrainee(TraineeDTO trainee, Credentials credentials) {
        userService.validateUserCredentials(credentials);

        Trainee tmpTrainee = traineeRepository.findByUser_Username(credentials.userName()).get();
        Optional<User> updatedUser = userService.updateUser(tmpTrainee.getUser().getId(), trainee.getUser());
        if (!updatedUser.isPresent()) {
            log.error("User update failed");
            throw new UpdateFailedException("User update failed");
        }

        tmpTrainee.setAddress(trainee.getAddress());
        tmpTrainee.setDateOfBirth(trainee.getDateOfBirth());
        tmpTrainee.setUser(updatedUser.get());

        return Optional.of(traineeRepository.save(tmpTrainee));
    }

    @Override
    public boolean changeActiveStatus(Boolean isActive, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        Trainee trainee = traineeRepository.findByUser_Username(credentials.userName()).get();
        return userService.changeActiveStatus(trainee.getUser().getId(), isActive);
    }

    @Override
    public boolean deleteTrainee(String userName, Credentials credentials) {
        userService.validateUserCredentials(credentials);

        boolean traineeDeleted = false;

        if (findTraineeByUsername(userName)) {
            traineeRepository.deleteByUser_Username(userName);
            traineeDeleted = true;
        }
        return traineeDeleted;
    }

    @Override
    public List<Trainer> getTrainers(String userName, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        findTraineeByUsername(userName);
        return trainingService.getTrainersOfTrainee(userName);
    }

    @Override
    public List<Trainer> getActiveTrainersNotAssignedTo(String userName, Credentials credentials) {
        userService.validateUserCredentials(credentials);

        List<Trainer> allTrainers = trainerService.getAllTrainers(credentials);
        List<Trainer> assignedTrainers = getTrainers(userName, credentials);

        if (allTrainers.isEmpty()) {
            log.info("No trainers found in the system");
            return Collections.emptyList();
        }

        if (!assignedTrainers.isEmpty()) {
            for (Trainer trainer : assignedTrainers) {
                if (trainer.getUser().getIsActive()) {
                    allTrainers.remove(trainer);
                }
            }
        }
        return allTrainers;
    }


    /*
     * Removes all the trainings of trainee with trainers who is not included in the trainerUsernames parameter
     */
    @Override
    public List<Trainer> updateTrainersList(String userName, List<String> trainerUsernames, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        findTraineeByUsername(userName);

        List<Trainer> tmpTrainers = trainingService.getTrainersOfTrainee(userName);
        List<String> toRemove = new ArrayList<>();

        if (tmpTrainers.isEmpty()) {
            log.warn("No Trainings with Trainers found, add training first");
        } else if (trainerUsernames.isEmpty()) {
            log.info("All trainers and trainings are deleted for user: {}", userName);
            toRemove = tmpTrainers.stream().map(trainer -> trainer.getUser().getUsername()).toList();
        } else {
            for (Trainer trainer : tmpTrainers) {
                if (!trainerUsernames.contains(trainer.getUser().getUsername())) {
                    toRemove.add(trainer.getUser().getUsername());
                }
            }
        }
        if (toRemove.isEmpty()) {
            log.info("No updates with TrainersList for User: {}", userName);
        } else {
            trainingService.deleteTrainingsWithTrainers(userName, toRemove);
        }
        return trainingService.getTrainersOfTrainee(userName);
    }

    @Override
    public List<Training> getTrainings(String userName, Date from, Date to, String trainerUserName, String trainingType, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        findTraineeByUsername(userName);
        List<Training> traineeTrainingsList = trainingService.getTraineeTrainingsList(userName, from, to, trainerUserName,
                trainingType);
        return traineeTrainingsList;
    }

    private Boolean findTraineeByUsername(String username) {
        Optional<Trainee> trainee = traineeRepository.findByUser_Username(username);
        if (!trainee.isPresent()) {
            log.error("Trainee with username {} not found, userName)", username);
            throw new ResourceNotFoundException("Trainee with username " + username + " not found");
        }
        return traineeRepository.findByUser_Username(username).isPresent();
    }

}
