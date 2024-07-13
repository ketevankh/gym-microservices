package com.example.trainer_workload.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "trainers")
@CompoundIndex(def = "{'firstName': 1, 'lastName': 1}", name = "name_index")
@Getter @Setter
public class Trainer {
    @Id
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private List<YearSummary> years = new ArrayList<>();
}

