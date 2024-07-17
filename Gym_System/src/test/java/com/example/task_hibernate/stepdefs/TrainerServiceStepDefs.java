package com.example.task_hibernate.stepdefs;

import com.example.task_hibernate.config.CucumberSpringConfiguration;
import com.example.task_hibernate.model.Trainer;
import com.example.task_hibernate.model.TrainingType;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.serviceDTOs.TrainerDTO;
import com.example.task_hibernate.model.dto.serviceDTOs.UserDTO;
import com.example.task_hibernate.service.TrainerService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@ContextConfiguration(classes = CucumberSpringConfiguration.class)
@SpringBootTest(classes = CucumberSpringConfiguration.class)
public class TrainerServiceStepDefs {

    @Autowired
    private TrainerService trainerService;

    private TrainerDTO trainerDTO;
    private Trainer createdTrainer;
    private Optional<Trainer> retrievedTrainer;
    private boolean isCreated;
    private Credentials credentials;

    @Given("I have a trainer's details")
    public void i_have_a_trainer_s_details() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Bob");
        userDTO.setLastName("Johnson");
        userDTO.setIsActive(true);

        trainerDTO = new TrainerDTO();
        trainerDTO.setUser(userDTO);
        trainerDTO.setSpecialization(new TrainingType(1L));
    }

    @When("I create a new trainer")
    public void i_create_a_new_trainer() {
        try {
            createdTrainer = trainerService.createTrainer(trainerDTO);
            isCreated = true;
        } catch (Exception e) {
            isCreated = false;
        }
    }

    @Then("the trainer should be created successfully")
    public void the_trainer_should_be_created_successfully() {
        assertTrue(isCreated);
        assertNotNull(createdTrainer);
        assertEquals("Bob", createdTrainer.getUser().getFirstName());
        assertEquals("Johnson", createdTrainer.getUser().getLastName());
    }

    @Given("I have an existing trainer ID")
    public void i_have_an_existing_trainer_id() {
        credentials = new Credentials("Bob.Johnson", "storedwith");
        createdTrainer = trainerService.getTrainerByUserName("Bob.Johnson", credentials).orElse(null);
        assertNotNull(createdTrainer);
    }

    @When("I retrieve the trainer by ID")
    public void i_retrieve_the_trainer_by_id() {
        if (createdTrainer != null) {
            retrievedTrainer = trainerService.getTrainerById(createdTrainer.getId(), credentials);
        }
    }

    @Then("the correct trainer should be returned")
    public void the_correct_trainer_should_be_returned() {
        assertTrue(retrievedTrainer.isPresent());
        assertEquals(createdTrainer.getId(), retrievedTrainer.get().getId());
    }
}
