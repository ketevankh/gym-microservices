package com.example.task_hibernate.service.impl;

import com.example.task_hibernate.exceptions.ResourceNotFoundException;
import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.TrainingType;
import com.example.task_hibernate.model.dto.controllerDTOs.request.WorkloadRequest;
import com.example.task_hibernate.model.enums.ActionType;
import com.example.task_hibernate.model.enums.ExerciseType;
import com.example.task_hibernate.repository.TraineeRepository;
import com.example.task_hibernate.repository.TrainerRepository;
import com.example.task_hibernate.repository.TrainingRepository;
import com.example.task_hibernate.service.TrainingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final JmsTemplate jmsTemplate;
    private static final String WORKLOAD_QUEUE = "workload.queue";

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

        sendWorkloadUpdate(trainer.get(), trainingDate, trainingDuration, ActionType.ADD);

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

        for (String trainerUsername : trainerUsernames) {
            Optional<Trainer> trainer = trainerRepository.findByUser_Username(trainerUsername);
            trainer.ifPresent(value -> sendWorkloadUpdate(value, null, 0, ActionType.DELETE));
        }

        return true;
    }

    @Override
    public List<TrainingType> getAllTrainingTypes() {
        return trainingRepository.findAllTrainingTypes();
    }

    @CircuitBreaker(name = "secondaryMicroservice", fallbackMethod = "fallbackSendWorkloadUpdate")
    public void sendWorkloadUpdate(Trainer trainer, Date trainingDate, int trainingDuration, ActionType actionType) {
        WorkloadRequest request = new WorkloadRequest();
        request.setTrainerUsername(trainer.getUser().getUsername());
        request.setTrainerFirstName(trainer.getUser().getFirstName());
        request.setTrainerLastName(trainer.getUser().getLastName());
        request.setIsActive(trainer.getUser().getIsActive());
        if (trainingDate != null) {
            request.setTrainingDate(trainingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        request.setTrainingDuration(trainingDuration);
        request.setActionType(actionType);

        jmsTemplate.convertAndSend(WORKLOAD_QUEUE, request, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException, javax.jms.JMSException {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getCredentials() instanceof String) {
                    String jwtToken = (String) authentication.getCredentials();
                    message.setStringProperty("Authorization", "Bearer " + jwtToken);
                }
                return message;
            }
        });
    }
}
