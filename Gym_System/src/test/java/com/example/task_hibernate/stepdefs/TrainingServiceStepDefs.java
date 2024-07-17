package com.example.task_hibernate.stepdefs;

import com.example.task_hibernate.TaskHibernateApplication;
import com.example.task_hibernate.model.Training;
import com.example.task_hibernate.service.TrainingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TaskHibernateApplication.class)
public class TrainingServiceStepDefs {

    @Autowired
    private TrainingService trainingService;

    private List<Training> trainings;
    private Training createdTraining;
    private Optional<Training> retrievedTraining;
    private String traineeUsername;
    private String trainerUsername;
    private Date fromDate;
    private Date toDate;

    @Given("I have trainings in the system")
    public void i_have_trainings_in_the_system() {
        // Assuming some trainings are already present in the system
        trainings = trainingService.getAllTrainings();
        assertFalse(trainings.isEmpty(), "No trainings found in the system");
    }

    @When("I retrieve all trainings")
    public void i_retrieve_all_trainings() {
        trainings = trainingService.getAllTrainings();
    }

    @Then("the trainings should be returned successfully")
    public void the_trainings_should_be_returned_successfully() {
        assertNotNull(trainings);
        assertFalse(trainings.isEmpty());
    }

    @Given("I have an existing training ID")
    public void i_have_an_existing_training_id() {
        createdTraining = trainingService.getAllTrainings().stream()
                .findFirst()
                .orElse(null);
        assertNotNull(createdTraining);
    }

    @When("I retrieve the training by ID")
    public void i_retrieve_the_training_by_id() {
        if (createdTraining != null) {
            retrievedTraining = trainingService.getTrainingById(createdTraining.getId());
        }
    }

    @Then("the correct training should be returned")
    public void the_correct_training_should_be_returned() {
        assertTrue(retrievedTraining.isPresent());
        assertEquals(createdTraining.getId(), retrievedTraining.get().getId());
    }

    @Given("I have a trainee with username {string}")
    public void i_have_a_trainee_with_username(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    @When("I retrieve trainings for the trainee from {string} to {string}")
    public void i_retrieve_trainings_for_the_trainee_from_to(String fromDateStr, String toDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.fromDate = sdf.parse(fromDateStr);
            this.toDate = sdf.parse(toDateStr);

            trainings = trainingService.getTraineeTrainingsList(traineeUsername, fromDate, toDate, null, null);
        } catch (Exception e) {
            fail("Failed to retrieve trainings for trainee: " + e.getMessage());
        }
    }

    @Then("the trainings for the trainee should be returned")
    public void the_trainings_for_the_trainee_should_be_returned() {
        assertNotNull(trainings);
    }

    @Given("I have a trainer with username {string}")
    public void i_have_a_trainer_with_username(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    @When("I retrieve trainings for the trainer from {string} to {string}")
    public void i_retrieve_trainings_for_the_trainer_from_to(String fromDateStr, String toDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.fromDate = sdf.parse(fromDateStr);
            this.toDate = sdf.parse(toDateStr);

            trainings = trainingService.getTrainerTrainingsList(trainerUsername, fromDate, toDate, null);
        } catch (Exception e) {
            fail("Failed to retrieve trainings for trainer: " + e.getMessage());
        }
    }

    @Then("the trainings for the trainer should be returned")
    public void the_trainings_for_the_trainer_should_be_returned() {
        assertNotNull(trainings);
    }
}
