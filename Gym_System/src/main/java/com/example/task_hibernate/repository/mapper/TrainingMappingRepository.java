package com.example.task_hibernate.repository.mapper;

import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.dto.controllerDTOs.response.TrainingTraineeResponseDTO;
import com.example.task_hibernate.model.dto.controllerDTOs.response.TrainingTrainerResponseDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingMappingRepository {
    public static List<TrainingTraineeResponseDTO> TrainingListToTrainingTraineeResponseDTOList(List<Training> trainings)  {
        List<TrainingTraineeResponseDTO> result = new ArrayList<>();
        for (Training training : trainings) {
            result.add(new TrainingTraineeResponseDTO(training.getTrainingName(), training.getTrainingType().getTrainingType().name(),
                    training.getTrainingDate(), training.getDuration(), training.getTrainer().getUser().getUsername()));
        }
        return result;
    }

    public static List<TrainingTrainerResponseDTO> TrainingListToTrainingTrainerResponseDTOList(List<Training> trainings)  {
        List<TrainingTrainerResponseDTO> result = new ArrayList<>();
        for (Training training : trainings) {
            result.add(new TrainingTrainerResponseDTO(training.getTrainingName(), training.getTrainingType().getTrainingType().name(),
                    training.getTrainingDate(), training.getDuration(), training.getTrainee().getUser().getUsername()));
        }
        return result;
    }
}
