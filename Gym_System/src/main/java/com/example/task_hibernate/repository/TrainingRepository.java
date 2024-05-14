package com.example.task_hibernate.repository;

import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.model.TrainingType;
import com.example.task_hibernate.model.enums.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    Optional<Training> findByTrainingName(String title);

    @Query("SELECT DISTINCT t.trainer FROM Training t " +
            "JOIN t.trainee trainee " +
            "JOIN t.trainer trainer " +
            "WHERE trainee.user.username = :username")
    List<Trainer> findTrainerByTraineeUsername(String username);

    @Query("SELECT DISTINCT t.trainer FROM Training t " +
            "JOIN t.trainee trainee " +
            "JOIN t.trainer trainer " +
            "WHERE trainer.user.username = :username")
    List<Trainee> findTraineeByTrainerUsername(String username);

    @Query("SELECT tt FROM TrainingType tt")
    List<TrainingType> findAllTrainingTypes();

    @Query("SELECT t FROM Training t " +
            "JOIN t.trainee.user u " +
            "LEFT JOIN t.trainer.user trainerU " +
            "WHERE u.username = :username " +
            "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
            "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
            "AND (:trainerName IS NULL OR trainerU.username = :trainerName) " +
            "AND (:trainingType IS NULL OR t.trainingType = :trainingType)")
    List<Training> findByTraineeUsernameAndCriteria(
            @Param("username") String username,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingType") ExerciseType trainingType
    );

    @Query("SELECT t FROM Training t " +
            "JOIN t.trainer.user u " +
            "LEFT JOIN t.trainee.user traineeU " +
            "WHERE u.username = :trainerUsername " +
            "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
            "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
            "AND (:traineeName IS NULL OR traineeU.username = :traineeName)")
    List<Training> findByTrainerUsernameAndCriteria(
            @Param("trainerUsername") String trainerUsername,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("traineeName") String traineeName
    );

    @Modifying
    @Query(value = "DELETE FROM trainings t " +
            "WHERE t.trainee_id IN (SELECT tr.id FROM trainees tr JOIN users utr ON tr.user_id = utr.id WHERE utr.user_name = :traineeUsername) " +
            "AND t.trainer_id IN (SELECT tn.id FROM trainers tn JOIN users utn ON tn.user_id = utn.id WHERE utn.user_name IN :trainerUsernames)",
            nativeQuery = true)
    void deleteByTraineeUserUserNameAndTrainerUserUserNameIn(String traineeUsername, List<String> trainerUsernames);
}
