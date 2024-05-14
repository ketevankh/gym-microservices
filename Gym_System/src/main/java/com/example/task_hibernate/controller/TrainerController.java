package com.example.task_hibernate.controller;

import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.TrainingType;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.controllerDTOs.request.TrainerRequestDTO;
import com.example.task_hibernate.model.dto.controllerDTOs.response.TrainerResponseDTO;
import com.example.task_hibernate.model.dto.controllerDTOs.response.TrainingTrainerResponseDTO;
import com.example.task_hibernate.model.dto.serviceDTOs.TrainerDTO;
import com.example.task_hibernate.model.dto.serviceDTOs.UserDTO;
import com.example.task_hibernate.repository.mapper.TrainerMappingRepository;
import com.example.task_hibernate.repository.mapper.TrainingMappingRepository;
import com.example.task_hibernate.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerMappingRepository trainerMapper;

    @PostMapping
    @Operation(summary = "register a new trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer registered successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Credentials> registerTrainer(
            @RequestParam(required = true) final String firstName,
            @RequestParam(required = true) final String lastName,
            @RequestParam(required = true) final Long trainingTypeId) {
        TrainerDTO trainerDTO = new TrainerDTO(new TrainingType(trainingTypeId), new UserDTO(firstName, lastName, true));
        Trainer trainer = trainerService.createTrainer(trainerDTO);
        Credentials credentials = new Credentials(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        return ResponseEntity.ok(credentials);
    }

    @GetMapping(path = "/{userName}")
    @Operation(summary = "Get trainer by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<TrainerResponseDTO> getTrainerByUserName(@PathVariable final String userName, @RequestHeader final String username, @RequestHeader final String password) {
        Credentials credentials = new Credentials(username, password);
        Trainer trainer = trainerService.getTrainerByUserName(userName, credentials).orElse(null);
        List<Trainee> trainees = trainerService.getTrainees(userName, credentials);
        return ResponseEntity.ok(trainerMapper.trainerToTraineeResponseDTO(trainer, trainees));
    }

    @PatchMapping(path = "/{userName}/activate-deactivate")
    @Operation(summary = "Activate or deactivate a trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "active status changed"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> activateDeactivate(@PathVariable final String userName, @RequestParam final Boolean isActive, @RequestHeader final String password) {
        Credentials credentials = new Credentials(userName, password);
        trainerService.changeActiveStatus(isActive, credentials);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(summary = "Update a trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<TrainerResponseDTO> updateTrainer(@RequestBody final TrainerRequestDTO trainerRequestDTO, @RequestHeader final String userName, @RequestHeader final String password) {
        Credentials credentials = new Credentials(userName, password);
        TrainerDTO trainerDTO = trainerMapper.trainerRequestDTOToTrainerDTO(trainerRequestDTO);
        Trainer trainer = trainerService.updateTrainer(trainerDTO, credentials).orElse(null);
        if (trainer == null) {
            return ResponseEntity.notFound().build();
        }
        List<Trainee> trainees = trainerService.getTrainees(trainer.getUser().getUsername(), credentials);
        return ResponseEntity.ok(trainerMapper.trainerToTraineeResponseDTO(trainer, trainees));
    }

    @GetMapping(path = "/{userName}/trainings")
    @Operation(summary = "Get trainings for a trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingTrainerResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<List<TrainingTrainerResponseDTO>> getTrainings(@PathVariable final String userName,
                                                                         @RequestParam final Date from,
                                                                         @RequestParam final Date to,
                                                                         @RequestParam final String trainerUserName,
                                                                         @RequestParam final String trainingType,
                                                                         @RequestHeader final String password) {
        Credentials credentials = new Credentials(userName, password);
        Trainer trainer = trainerService.getTrainerByUserName(userName, credentials).orElse(null);
        if (trainer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Training> trainings = trainerService.getTrainings(userName, from, to, trainerUserName, trainingType, credentials);
        return ResponseEntity.ok(TrainingMappingRepository.TrainingListToTrainingTrainerResponseDTOList(trainings));
    }
}
