package com.example.task_hibernate.service.impl;

public interface TrainerMonthlySummaryService {
    void addWorkload(int month, int hours);
    void removeWorkload(int month, int hours);
}
