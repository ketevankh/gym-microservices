package com.example.trainer_workload.model;

import lombok.Data;
import java.util.Map;

@Data
public class MonthlySummary {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean trainerStatus;
    private Map<Integer, Map<Integer, Integer>> years;
}
