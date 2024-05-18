package com.example.trainer_workload.service.impl;


import com.example.trainer_workload.model.MonthlySummary;
import com.example.trainer_workload.model.request.WorkloadRequest;
import com.example.trainer_workload.service.WorkloadService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkloadServiceImpl implements WorkloadService {
    public final Map<String, MonthlySummary> trainersWorkload = new HashMap<>();

    @Override
    public void updateWorkload(WorkloadRequest request) throws Exception {
        LocalDate localDate = request.getTrainingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Integer year = localDate.getYear();
        Integer month = localDate.getMonthValue();
        Integer duration = request.getTrainingDuration();

        String username = request.getTrainerUsername();
        MonthlySummary summary = trainersWorkload.computeIfAbsent(username, k -> {
            MonthlySummary newSummary = new MonthlySummary();
            newSummary.setTrainerUsername(username);
            newSummary.setTrainerFirstName(request.getTrainerFirstName());
            newSummary.setTrainerLastName(request.getTrainerLastName());
            newSummary.setTrainerStatus(request.getIsActive());
            newSummary.setYears(new HashMap<>());
            return newSummary;
        });

        switch(request.getActionType()) {
            case ADD:
                incrementMonthlySummary(summary.getYears(), year, month, duration);
                break;
            case DELETE:
                decrementMonthlySummary(summary.getYears(), year, month, duration);
                break;
            default:
                throw new IllegalArgumentException("Invalid action type");
        }
    }

    public void decrementMonthlySummary(Map<Integer, Map<Integer, Integer>> years, Integer year, Integer month, Integer duration) throws Exception {
        if(!years.containsKey((year))) {
            throw new Exception("Trainer does not have any training in the given year to delete");
        }
        Map<Integer, Integer> months = years.get(year);
        if(!months.containsKey(month)) {
            throw new Exception("Trainer does not have any training in the given month to delete");
        }
        Integer currentDuration = months.get(month);
        if(currentDuration < duration) {
            throw new Exception("Trainer does not have enough training duration in the given month to delete");
        }
        months.put(month, currentDuration - duration);
    }

    private void incrementMonthlySummary(Map<Integer, Map<Integer, Integer>> years, Integer year, Integer month, Integer duration) {
        years.putIfAbsent(year, new HashMap<>());
        Map<Integer, Integer> months = years.get(year);
        months.put(month, months.getOrDefault(month, 0) + duration);
    }
}
