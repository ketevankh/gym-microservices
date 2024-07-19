package com.example.trainer_workload.stepdefs;

import com.example.trainer_workload.config.CucumberSpringConfiguration;
import com.example.trainer_workload.model.Trainer;
import com.example.trainer_workload.model.WorkloadRequest;
import com.example.trainer_workload.service.TrainerService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = CucumberSpringConfiguration.class)
public class IntegrationStepDefs {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private JmsTemplate jmsTemplate;

    private WorkloadRequest workloadRequest;
    private Optional<Trainer> retrievedTrainer;

    @Given("a workload request for user {string} to add training")
    public void a_workload_request_for_user_to_add_training(String username) {
        workloadRequest = new WorkloadRequest(username, "FirstName", "LastName", true, LocalDate.now(), 60, "ADD");
    }

    @When("the request is sent to the queue")
    public void the_request_is_sent_to_the_queue() {
        jmsTemplate.convertAndSend("workload.queue", workloadRequest);
    }

    @Then("the trainer {string} should be updated with training details")
    public void the_trainer_should_be_updated_with_training_details(String username) {
        retrievedTrainer = trainerService.getTrainerByUsername(username);
        assertTrue(retrievedTrainer.isPresent());
    }

    @Given("a workload request for user {string} to delete training")
    public void a_workload_request_for_user_to_delete_training(String username) {
        workloadRequest = new WorkloadRequest(username, "FirstName", "LastName", true, LocalDate.now(), 60, "DELETE");
    }

    @When("the delete request is sent to the queue")
    public void the_delete_request_is_sent_to_the_queue() {
        jmsTemplate.convertAndSend("workload.queue", workloadRequest);
    }

    @Then("the trainer {string} should have the training deleted")
    public void the_trainer_should_have_the_training_deleted(String username) {
        retrievedTrainer = trainerService.getTrainerByUsername(username);
        assertTrue(retrievedTrainer.isPresent());
    }
}

