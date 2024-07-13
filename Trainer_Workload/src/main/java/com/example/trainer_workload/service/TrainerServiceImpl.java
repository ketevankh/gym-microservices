package com.example.trainer_workload.service;

import com.example.trainer_workload.model.Trainer;
import com.example.trainer_workload.model.YearSummary;
import com.example.trainer_workload.model.MonthSummary;
import com.example.trainer_workload.model.WorkloadRequest;
import com.example.trainer_workload.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final JmsTemplate jmsTemplate;
    private final TrainerRepository trainerRepository;

    private static final String ACTION_ADD = "ADD";
    private static final String ACTION_DELETE = "DELETE";

    @Value("${dlq.name}")
    private String DLQ;

    @Override
    public Optional<Trainer> getTrainerByUsername(String username) {
        return trainerRepository.findByUsername(username);
    }

    @Override
    public Trainer saveTrainer(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    @Override
    public void deleteTrainer(String username) {
        if (trainerRepository.findByUsername(username).isPresent()) {
            trainerRepository.deleteById(username);
        }
    }

    @Override
    @Transactional
    @JmsListener(destination = "workload.queue")
    public Trainer handleTrainerWorkload(String username, String firstName, String lastName, boolean isActive, LocalDate trainingDate, int trainingDuration, String actionType) {
        try {
            Trainer trainer = trainerRepository.findByUsername(username).orElse(new Trainer());
            trainer.setUsername(username);
            trainer.setFirstName(firstName);
            trainer.setLastName(lastName);
            trainer.setStatus(isActive);

            if (trainer.getYears() == null) {
                trainer.setYears(new ArrayList<>());
            }

            if (ACTION_ADD.equalsIgnoreCase(actionType)) {
                addTrainingSummary(trainer, trainingDate, trainingDuration);
            } else if (ACTION_DELETE.equalsIgnoreCase(actionType)) {
                deleteTrainingSummary(trainer, trainingDate, trainingDuration);
            } else {
                throw new IllegalArgumentException("Invalid action type: " + actionType);
            }

            return trainerRepository.save(trainer);

        } catch (Exception e) {
            sendToDLQ(username, firstName, lastName, isActive, trainingDate, trainingDuration, actionType);
            throw e;
        }
    }

    private void addTrainingSummary(Trainer trainer, LocalDate trainingDate, int trainingDuration) {
        for (YearSummary yearSummary : trainer.getYears()) {
            if (yearSummary.getYear() == trainingDate.getYear()) {
                for (MonthSummary monthSummary : yearSummary.getMonths()) {
                    if (monthSummary.getMonth() == trainingDate.getMonthValue()) {
                        monthSummary.setTrainingsSummaryDuration(monthSummary.getTrainingsSummaryDuration() + trainingDuration);
                        return;
                    }
                }
                MonthSummary newMonthSummary = new MonthSummary();
                newMonthSummary.setMonth(trainingDate.getMonthValue());
                newMonthSummary.setTrainingsSummaryDuration(trainingDuration);
                if (yearSummary.getMonths() == null) {
                    yearSummary.setMonths(new ArrayList<>());
                }
                yearSummary.getMonths().add(newMonthSummary);
                return;
            }
        }

        YearSummary newYearSummary = new YearSummary();
        newYearSummary.setYear(trainingDate.getYear());
        MonthSummary newMonthSummary = new MonthSummary();
        newMonthSummary.setMonth(trainingDate.getMonthValue());
        newMonthSummary.setTrainingsSummaryDuration(trainingDuration);
        newYearSummary.setMonths(new ArrayList<>(List.of(newMonthSummary)));
        trainer.getYears().add(newYearSummary);
    }

    private void deleteTrainingSummary(Trainer trainer, LocalDate trainingDate, int trainingDuration) {
        for (YearSummary yearSummary : trainer.getYears()) {
            if (yearSummary.getYear() == trainingDate.getYear()) {
                if (yearSummary.getMonths() != null) {
                    yearSummary.getMonths().removeIf(monthSummary ->
                            monthSummary.getMonth() == trainingDate.getMonthValue() &&
                                    monthSummary.getTrainingsSummaryDuration() == trainingDuration);
                }
                return;
            }
        }
    }

    private void sendToDLQ(String username, String firstName, String lastName, boolean isActive, LocalDate trainingDate, int trainingDuration, String actionType) {
        jmsTemplate.convertAndSend(DLQ, new WorkloadRequest(username, firstName, lastName, isActive, trainingDate, trainingDuration, actionType));
    }
}
