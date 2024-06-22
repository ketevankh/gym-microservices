package com.example.trainer_workload.controller;


import com.example.trainer_workload.model.Trainer;
import com.example.trainer_workload.model.WorkloadRequest;
import com.example.trainer_workload.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Operation(summary = "Handle trainer workload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer workload handled successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Trainer.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping("/workload")
    public ResponseEntity<Trainer> handleTrainerWorkload(
            @RequestBody WorkloadRequest workloadRequest) {

        Trainer updatedTrainer = trainerService.handleTrainerWorkload(
                workloadRequest.getTrainerUsername(),
                workloadRequest.getTrainerFirstName(),
                workloadRequest.getTrainerLastName(),
                workloadRequest.isActive(),
                workloadRequest.getTrainingDate(),
                workloadRequest.getTrainingDuration(),
                workloadRequest.getActionType());

        return new ResponseEntity<>(updatedTrainer, HttpStatus.OK);
    }

    @Operation(summary = "Get trainer by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the trainer",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Trainer.class)) }),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content)
    })
    @GetMapping("/{username}")
    public ResponseEntity<Trainer> getTrainerByUsername(@Parameter(description = "Trainer's username") @PathVariable String username) {
        return trainerService.getTrainerByUsername(username)
                .map(trainer -> new ResponseEntity<>(trainer, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Save a new trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Trainer.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Trainer> saveTrainer(@Parameter(description = "Trainer object") @RequestBody Trainer trainer) {
        Trainer savedTrainer = trainerService.saveTrainer(trainer);
        return new ResponseEntity<>(savedTrainer, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a trainer by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainer deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content)
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainer(@Parameter(description = "Trainer's username") @PathVariable String username) {
        trainerService.deleteTrainer(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
