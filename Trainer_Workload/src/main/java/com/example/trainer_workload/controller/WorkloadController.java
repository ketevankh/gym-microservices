package com.example.trainer_workload.controller;

import com.example.trainer_workload.model.request.WorkloadRequest;
import com.example.trainer_workload.service.WorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @Operation(summary = "Update workload", description = "Updates the workload of a trainer based on the given request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workload updated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Failed to update workload",
                    content = @Content)
    })
    @PostMapping("/update")
    public ResponseEntity<String> updateWorkload(@Validated @RequestBody WorkloadRequest request) throws Exception {
        workloadService.updateWorkload(request);
        return ResponseEntity.ok("Workload updated successfully.");
    }
}