package com.example.task_hibernate.controller;

import com.example.task_hibernate.model.TrainingType;
import com.example.task_hibernate.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Tag(name = "Training API", description = "API for managing trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

    @GetMapping("/trainingTypes")
    @Operation(summary = "Get all training types", description = "Retrieve a list of all available training types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of training types"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TrainingType>> getTrainingTypes() {
        logger.info("GET /trainingTypes called");
        return ResponseEntity.ok(trainingService.getAllTrainingTypes());
    }

    @PostMapping("/addTraining")
    @Operation(summary = "Add a new training", description = "Add a new training session for a trainee with a specific trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added the training"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> addTraining(
            @RequestParam @Parameter(description = "Username of the trainee", required = true) String traineeUserName,
            @RequestParam @Parameter(description = "Username of the trainer", required = true) String trainerUserName,
            @RequestParam @Parameter(description = "Name of the training", required = true) String trainingName,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
            @Parameter(description = "Training date in the format 'yyyy-MM-dd HH:mm'", required = true) LocalDateTime trainingDate,
            @RequestParam @Parameter(description = "Duration of the training in minutes", required = true) int trainingDuration) {
        logger.info("POST /addTraining called with traineeUserName={}, trainerUserName={}, trainingName={}, trainingDate={}, trainingDuration={}",
                traineeUserName, trainerUserName, trainingName, trainingDate, trainingDuration);
        Date date = Date.from(trainingDate.atZone(ZoneId.systemDefault()).toInstant());
        if (!trainingService.addTraining(traineeUserName, trainerUserName, trainingName, date, trainingDuration)) {
            logger.error("Failed to add training for traineeUserName={} and trainerUserName={}", traineeUserName, trainerUserName);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteTraining")
    @Operation(summary = "Delete trainings", description = "Delete training sessions for a trainee with specific trainers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the trainings"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTraining(
            @RequestParam @Parameter(description = "Username of the trainee", required = true) String traineeUserName,
            @RequestParam @Parameter(description = "List of usernames of the trainers", required = true) List<String> trainerUsernames) {
        logger.info("DELETE /deleteTraining called with traineeUserName={} and trainerUsernames={}", traineeUserName, trainerUsernames);
        if (!trainingService.deleteTrainingsWithTrainers(traineeUserName, trainerUsernames)) {
            logger.error("Failed to delete trainings for traineeUserName={} and trainerUsernames={}", traineeUserName, trainerUsernames);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
}
