package com.example.task_hibernate.model.dto.serviceDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private Boolean isActive;
}
