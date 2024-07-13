//package com.example.trainer_workload.model;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@Entity
//public class Trainer {
//    @Id
//    private String trainerUsername;
//    private String trainerFirstName;
//    private String trainerLastName;
//    private boolean isActive;
//
//    @ElementCollection
//    @CollectionTable(name = "trainer_training_summary", joinColumns = @JoinColumn(name = "trainer_id"))
//    private List<TrainingSummary> trainingSummary = new ArrayList<>();
//}
//
