package com.example.trainer_workload.controller;

import com.example.trainer_workload.model.request.WorkloadRequest;
import com.example.trainer_workload.service.WorkloadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workload")
public class WorkloadController {

    private final WorkloadService workloadService;

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateWorkload(@RequestBody WorkloadRequest request) throws Exception {
        workloadService.updateWorkload(request);
        return ResponseEntity.ok("Workload updated successfully.");
    }
}