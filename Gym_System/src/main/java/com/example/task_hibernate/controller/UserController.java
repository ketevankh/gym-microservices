package com.example.task_hibernate.controller;

import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        if (!userService.validateUserCredentials(new Credentials(username, password))) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok().body("Welcome " + username);
        }
    }

    @PutMapping
    @Operation(summary = "Change Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    public ResponseEntity<Void> changeLogin(@RequestParam String userName, @RequestParam String oldPassword, @RequestParam String newPassword) {
        if (userService.validateUserCredentials(new Credentials(userName, oldPassword))) {
            if (userService.changeUserPassword(newPassword, userName)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
