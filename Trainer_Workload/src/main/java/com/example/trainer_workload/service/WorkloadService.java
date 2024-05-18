package com.example.trainer_workload.service;

import com.example.trainer_workload.model.request.WorkloadRequest;

public interface WorkloadService {
    void updateWorkload(WorkloadRequest request) throws Exception;
}
