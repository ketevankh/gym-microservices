package com.example.task_hibernate.controller;

import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.controllerDTOs.response.TrainingTraineeResponseDTO;
import com.example.task_hibernate.model.dto.serviceDTOs.TraineeDTO;
import com.example.task_hibernate.model.dto.controllerDTOs.response.TraineeResponseDTO;
import com.example.task_hibernate.model.dto.controllerDTOs.response.TrainerDTOForList;
import com.example.task_hibernate.model.dto.controllerDTOs.request.TraineeRequestDTO;
import com.example.task_hibernate.repository.mapper.TraineeMappingRepository;
import com.example.task_hibernate.repository.mapper.TrainerMappingRepository;
import com.example.task_hibernate.repository.mapper.TrainingMappingRepository;
import com.example.task_hibernate.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
@Validated
public class TraineeController {

    private final TraineeService traineeService;
    private final TraineeMappingRepository traineeMapper;

    @PostMapping
    @Operation(summary = "register a new trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee registered successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Credentials> registerTrainee(@Valid @RequestBody final TraineeRequestDTO traineeRequestDTO) {
        TraineeDTO traineeDTO = traineeMapper.traineeRequestDTOToTraineeDTO(traineeRequestDTO);
        Trainee trainee = traineeService.createTrainee(traineeDTO);
        Credentials credentials = new Credentials(trainee.getUser().getUsername(), trainee.getUser().getPassword());
        return ResponseEntity.ok(credentials);
    }


    @GetMapping(path = "/{username}")
    @Operation(summary = "get trainee by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<TraineeResponseDTO> getTraineeByUsername(@Valid @PathVariable final String username, @RequestHeader final String userName, @RequestHeader final String password) {
        Credentials credentials = new Credentials(userName, password);
        Trainee trainee = traineeService.getTraineeByUsername(username, credentials).orElse(null);
        List<Trainer> trainers = traineeService.getTrainers(username, credentials);
        return ResponseEntity.ok(traineeMapper.traineeToTraineeResponseDTO(trainee, trainers));
    }


    @GetMapping(path = "/{userName}/trainings")
    @Operation(summary = "Get trainings for a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingTraineeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<List<TrainingTraineeResponseDTO>> getTrainings(@PathVariable final String userName,
                                                                         @RequestParam(required = false) final Date from,
                                                                         @RequestParam(required = false) final Date to,
                                                                         @RequestParam(required = false) final String trainerUserName,
                                                                         @RequestParam(required = false) final String trainingType,
                                                                         @RequestHeader final String password) {
        Credentials credentials = new Credentials(userName, password);
        List<Training> trainings = traineeService.getTrainings(userName, from, to, trainerUserName, trainingType, credentials);
        return ResponseEntity.ok(TrainingMappingRepository.TrainingListToTrainingTraineeResponseDTOList(trainings));
    }

    @GetMapping(path = "/{userName}/NotAssignedTrainers")
    @Operation(summary = "Get active trainers who aren't assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingTraineeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<List<TrainerDTOForList>> getActiveTrainersNotAssignedTo(@PathVariable final String userName, @RequestHeader final String password) {
        Credentials credentials = new Credentials(userName, password);
        List<Trainer> trainers = traineeService.getActiveTrainersNotAssignedTo(userName, credentials);
        return ResponseEntity.ok(TrainerMappingRepository.trainerListToTrainerDTOForList(trainers));
    }

    @PutMapping
    @Operation(summary = "Update trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingTraineeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Trainee update failed")
    })
    public ResponseEntity<TraineeResponseDTO> updateTrainee(@Valid @RequestBody final TraineeRequestDTO traineeRequestDTO,
                                                            @RequestHeader final String userName,
                                                            @RequestHeader final String password) {
        Credentials credentials = new Credentials(userName, password);
        TraineeDTO traineeDTO = traineeMapper.traineeRequestDTOToTraineeDTO(traineeRequestDTO);
        Trainee trainee = traineeService.updateTrainee(traineeDTO, credentials).orElse(null);
        List<Trainer> trainers = traineeService.getTrainers(trainee.getUser().getUsername(), credentials);
        return ResponseEntity.ok(traineeMapper.traineeToTraineeResponseDTO(trainee, trainers));
    }

    @DeleteMapping
    @Operation(summary = "Delete trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deleted"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Void> deleteTrainee(@RequestParam final String userNameToDelete, @RequestHeader final String userName, @RequestHeader final String password) {
        Credentials credentials = new Credentials(userName, password);
        traineeService.deleteTrainee(userNameToDelete, credentials);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{userName}/TrainersList")
    @Operation(summary = "Update trainers list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers list updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingTraineeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<List<TrainerDTOForList>> updateTrainersList(@PathVariable final String userName, @RequestHeader final String password, @RequestBody final List<String> trainersUserNames) {
        Credentials credentials = new Credentials(userName, password);
        List<Trainer> result = traineeService.updateTrainersList(userName, trainersUserNames, credentials);
        return ResponseEntity.ok(TrainerMappingRepository.trainerListToTrainerDTOForList(result));
    }

    @PatchMapping(path = "/{userName}/activeStatus")
    @Operation(summary = "Activate or deactivate trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "active status changed"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Void> activateDeactivate(@PathVariable final String userName,
                                                   @RequestParam final Boolean isActive,
                                                   @RequestHeader final String password) {
        Credentials credentials = new Credentials(userName, password);
        traineeService.changeActiveStatus(isActive, credentials);
        return ResponseEntity.ok().build();
    }
}
