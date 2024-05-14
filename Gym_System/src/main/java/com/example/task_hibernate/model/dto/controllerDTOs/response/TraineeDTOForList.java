package com.example.task_hibernate.model.dto.controllerDTOs.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeDTOForList {
    private String firstName;
    private String lastName;
    private String userName;
}
