package com.example.task_hibernate.controller;

import com.example.task_hibernate.model.TrainingType;
import com.example.task_hibernate.model.User;
import com.example.task_hibernate.model.dto.controllerDTOs.request.WorkloadRequest;
import com.example.task_hibernate.model.enums.ActionType;
import com.example.task_hibernate.service.TrainingService;
import com.example.task_hibernate.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);
    private static final String WORKLOAD_SERVICE_URL = "http://workload/update";

    private final TrainingService trainingService;
    private final UserService userService;
    private final RestTemplate restTemplate;

    @GetMapping("/trainingTypes")
    public ResponseEntity<List<TrainingType>> getTrainingTypes() {
        return ResponseEntity.ok(trainingService.getAllTrainingTypes());
    }

    @PostMapping("/addTraining")
    public ResponseEntity<Void> addTraining(@RequestParam String traineeUserName,
                                            @RequestParam String trainerUserName,
                                            @RequestParam String trainingName,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
                                                @Parameter(description = "Training date in the format 'yyyy-MM-dd HH:mm'", required = true) LocalDateTime trainingDate,
                                            @RequestParam int trainingDuration) {
        Date date = Date.from(trainingDate.atZone(ZoneId.systemDefault()).toInstant());
        if (!trainingService.addTraining(traineeUserName, trainerUserName, trainingName, date, trainingDuration)) {
            return ResponseEntity.badRequest().build();
        } else {
            User trainer = userService.getUserByUserName(trainerUserName).orElseThrow();

            WorkloadRequest request = new WorkloadRequest();
            request.setTrainerUsername(trainerUserName);
            request.setTrainerFirstName(trainer.getFirstName());
            request.setTrainerLastName(trainer.getLastName());
            request.setIsActive(trainer.getIsActive());
            request.setTrainingDate(trainingDate);
            request.setTrainingDuration(trainingDuration);
            request.setActionType(ActionType.valueOf("ADD"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<WorkloadRequest> entity = new HttpEntity<>(request, headers);

            try {
                restTemplate.postForEntity(WORKLOAD_SERVICE_URL, entity, String.class);
                logger.info("Successfully called workload service to update workload.");
            } catch (Exception e) {
                logger.error("Error while calling workload service: ", e);
                return ResponseEntity.status(500).build();
            }
            return ResponseEntity.ok().build();
        }
    }
}
