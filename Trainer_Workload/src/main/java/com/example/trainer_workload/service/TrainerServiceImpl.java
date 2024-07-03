package com.example.trainer_workload.service;

import com.example.trainer_workload.model.Trainer;
import com.example.trainer_workload.model.TrainingSummary;
import com.example.trainer_workload.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;

    private static final String ACTION_ADD = "ADD";
    private static final String ACTION_DELETE = "DELETE";

    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Optional<Trainer> getTrainerByUsername(String username) {
        return trainerRepository.findById(username);
    }

    @Override
    public Trainer saveTrainer(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    @Override
    public void deleteTrainer(String username) {
        trainerRepository.deleteById(username);
    }

    @Override
    public Trainer handleTrainerWorkload(String username, String firstName, String lastName, boolean isActive, LocalDate trainingDate, int trainingDuration, String actionType) {
        Trainer trainer = trainerRepository.findById(username).orElse(new Trainer());

        trainer.setTrainerUsername(username);
        trainer.setTrainerFirstName(firstName);
        trainer.setTrainerLastName(lastName);
        trainer.setActive(isActive);


        TrainingSummary trainingSummary = new TrainingSummary();
        trainingSummary.setTrainingYear(trainingDate.getYear());
        trainingSummary.setTrainingMonth(trainingDate.getMonthValue());
        trainingSummary.setTrainingDuration(trainingDuration);

        if (ACTION_ADD.equalsIgnoreCase(actionType)) {
            trainer.getTrainingSummary().add(trainingSummary);
        } else if (ACTION_DELETE.equalsIgnoreCase(actionType)) {
            trainer.getTrainingSummary().removeIf(summary ->
                    summary.getTrainingYear() == trainingSummary.getTrainingYear() &&
                            summary.getTrainingMonth() == trainingSummary.getTrainingMonth() &&
                            summary.getTrainingDuration() == trainingSummary.getTrainingDuration());
        }

        return trainerRepository.save(trainer);
    }
}
