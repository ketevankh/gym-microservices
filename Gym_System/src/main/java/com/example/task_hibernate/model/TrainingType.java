package com.example.task_hibernate.model;

import com.example.task_hibernate.model.enums.ExerciseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TRAINING_TYPES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingType {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private ExerciseType trainingType;

    public TrainingType(Long id) {
        this.id = id;
        if (id >= 0 && id < ExerciseType.values().length) {
            this.trainingType = ExerciseType.values()[(int) (long) id];
        } else {
            throw new IllegalArgumentException("Invalid id for ExerciseType: " + id);
        }
    }
}
