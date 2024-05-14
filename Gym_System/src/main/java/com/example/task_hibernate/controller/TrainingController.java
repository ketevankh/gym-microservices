package com.example.task_hibernate.controller;

import com.example.task_hibernate.model.TrainingType;
import com.example.task_hibernate.service.TrainingService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

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
            return ResponseEntity.ok().build();
        }
    }
}
