package com.example.trainer_workload.repository;

import com.example.trainer_workload.model.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrainerRepository extends MongoRepository<Trainer, String> {
    Optional<Trainer> findByUsername(String username);
}

