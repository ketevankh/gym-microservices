package com.example.task_hibernate.service.impl;

import com.example.task_hibernate.exceptions.ResourceNotFoundException;
import com.example.task_hibernate.exceptions.UpdateFailedException;
import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.User;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.serviceDTOs.TrainerDTO;
import com.example.task_hibernate.repository.TrainerRepository;
import com.example.task_hibernate.service.TrainerService;
import com.example.task_hibernate.service.TrainingService;
import com.example.task_hibernate.service.UserService;
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
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final TrainingService trainingService;

    public Trainer createTrainer(TrainerDTO trainerDTO) {
        User user = userService.createUser(trainerDTO.getUser());
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setTrainingType(trainerDTO.getSpecialization());
        return trainerRepository.save(trainer);
    }

    @Override
    public List<Trainer> getAllTrainers(Credentials credentials) {
        userService.validateUserCredentials(credentials);
        return trainerRepository.findAll();
    }

    @Override
    public Optional<Trainer> getTrainerById(Long id, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        return trainerRepository.findById(id);
    }

    @Override
    public Optional<Trainer> getTrainerByUserName(String userName, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        return trainerRepository.findByUser_Username(userName);
    }
    @Override
    public boolean changeTrainerPassword(String password, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        return userService.changeUserPassword(password, credentials.userName());
    }

    @Override
    public Optional<Trainer> updateTrainer(TrainerDTO trainer, Credentials credentials) {
        userService.validateUserCredentials(credentials);

        Trainer tmpTrainer = trainerRepository.findByUser_Username(credentials.userName()).get();
        Optional<User> updatedUser = userService.updateUser(tmpTrainer.getUser().getId(), trainer.getUser());
        if (!updatedUser.isPresent()) {
            log.error("User update failed");
            throw new UpdateFailedException("User update failed");
        }

        tmpTrainer.setTrainingType(trainer.getSpecialization());
        tmpTrainer.setUser(updatedUser.get());
        return Optional.of(trainerRepository.save(tmpTrainer));
    }

    @Override
    public boolean changeActiveStatus(Boolean isActive, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        Trainer trainer = trainerRepository.findByUser_Username(credentials.userName()).get();
        return userService.changeActiveStatus(trainer.getUser().getId(), isActive);
    }

    @Override
    public boolean deleteTrainer(String userName, Credentials credentials) {
        userService.validateUserCredentials(credentials);

        boolean trainerDeleted = false;

        if (findTrainerWithUsername(userName)) {
            trainerRepository.deleteByUser_Username(userName);
            trainerDeleted = true;
        }
        return trainerDeleted;
    }

    @Override
    public List<Trainee> getTrainees(String userName, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        findTrainerWithUsername(userName);
        return trainingService.getTraineesOfTrainer(userName);
    }

    @Override
    public List<Training> getTrainings(String userName, Date from, Date to, String trainerUserName, String trainingType, Credentials credentials) {
        userService.validateUserCredentials(credentials);
        findTrainerWithUsername(userName);
        return trainingService.getTrainerTrainingsList(userName, from, to, trainerUserName);
    }

    private Boolean findTrainerWithUsername(String username) {
        Optional<Trainer> trainer = trainerRepository.findByUser_Username(username);
        if (!trainer.isPresent()) {
            log.error("Trainer with username {} not found, userName)", username);
            throw new ResourceNotFoundException("Trainer with username " + username + " not found");
        }
        return trainerRepository.findByUser_Username(username).isPresent();
    }
}
