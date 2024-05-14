package com.example.task_hibernate.model.dto.controllerDTOs.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeRequestDTO {
    @NotNull(message = "First name is mandatory")
    private String firstName;

    @NotNull(message = "Last name is mandatory")
    private String lastName;

    private Date dateOfBirth;

    private String address;

    @NotNull(message = "Username is mandatory")
    private Boolean isActive = true;
}
