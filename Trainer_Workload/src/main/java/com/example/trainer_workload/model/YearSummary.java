package com.example.trainer_workload.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class YearSummary {
    private int year;
    private List<MonthSummary> months;
}
