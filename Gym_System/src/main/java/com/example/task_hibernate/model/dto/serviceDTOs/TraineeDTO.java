package com.example.task_hibernate.model.dto.serviceDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeDTO {
    private String address;
    private Date dateOfBirth;
    private UserDTO user;
}
