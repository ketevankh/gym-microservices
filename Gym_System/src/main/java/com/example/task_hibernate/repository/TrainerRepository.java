package com.example.task_hibernate.repository;

import com.example.task_hibernate.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUser_Username(String userName);
    void deleteByUser_Username(String userName);

}
