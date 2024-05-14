package com.example.task_hibernate.repository;

import com.example.task_hibernate.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUser_Username(String userName);
    void deleteByUser_Username(String userName);
}
