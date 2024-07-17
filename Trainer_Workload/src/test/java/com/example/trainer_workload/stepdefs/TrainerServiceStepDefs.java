package com.example.trainer_workload.stepdefs;

import com.example.trainer_workload.config.CucumberSpringConfiguration;
import com.example.trainer_workload.model.Trainer;
import com.example.trainer_workload.repository.TrainerRepository;
import com.example.trainer_workload.service.TrainerService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@CucumberContextConfiguration
@SpringBootTest(classes = CucumberSpringConfiguration.class)
public class TrainerServiceStepDefs {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainerRepository trainerRepository;

    @MockBean
    private JmsTemplate jmsTemplate;

    private Trainer trainer;
    private Optional<Trainer> retrievedTrainer;

    @Before
    public void setUp() {
        trainerRepository.deleteAll();
    }

    @Given("a trainer with username {string} exists")
    public void a_trainer_with_username_exists(String username) {
        trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setStatus(true);
        trainerService.saveTrainer(trainer);
    }

    @When("I retrieve the trainer by username {string}")
    public void i_retrieve_the_trainer_by_username(String username) {
        retrievedTrainer = trainerService.getTrainerByUsername(username);
    }

    @Then("the trainer with username {string} should be returned")
    public void the_trainer_with_username_should_be_returned(String username) {
        assertTrue(retrievedTrainer.isPresent());
        assertEquals(username, retrievedTrainer.get().getUsername());
    }

    @Given("I have a trainer with username {string}")
    public void i_have_a_trainer_with_username(String username) {
        trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setStatus(true);
    }

    @When("I save the trainer")
    public void i_save_the_trainer() {
        trainerService.saveTrainer(trainer);
    }

    @Then("the trainer with username {string} should be saved successfully")
    public void the_trainer_with_username_should_be_saved_successfully(String username) {
        Optional<Trainer> savedTrainer = trainerService.getTrainerByUsername(username);
        assertTrue(savedTrainer.isPresent());
        assertEquals(username, savedTrainer.get().getUsername());
    }

    @When("I delete the trainer by username {string}")
    public void i_delete_the_trainer_by_username(String username) {
        doNothing().when(jmsTemplate).convertAndSend(any(String.class), any(Object.class));
        trainerService.deleteTrainer(username);
    }

    @Then("the trainer with username {string} should be deleted successfully")
    public void the_trainer_with_username_should_be_deleted_successfully(String username) {
        Optional<Trainer> deletedTrainer = trainerService.getTrainerByUsername(username);
        assertFalse(deletedTrainer.isPresent());
    }
}