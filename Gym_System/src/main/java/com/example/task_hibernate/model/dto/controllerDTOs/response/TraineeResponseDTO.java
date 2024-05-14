package com.example.task_hibernate.model.dto.controllerDTOs.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeResponseDTO {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String Address;
    private Boolean isActive;
    private List<TrainerDTOForList> trainersList;
}
