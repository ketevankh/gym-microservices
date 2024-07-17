package com.example.task_hibernate.stepdefs;

import com.example.task_hibernate.config.CucumberSpringConfiguration;
import com.example.task_hibernate.model.Trainee;
import com.example.task_hibernate.model.dto.Credentials;
import com.example.task_hibernate.model.dto.serviceDTOs.TraineeDTO;
import com.example.task_hibernate.model.dto.serviceDTOs.UserDTO;
import com.example.task_hibernate.service.TraineeService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = CucumberSpringConfiguration.class)
public class TraineeServiceStepDefs {

    @Autowired
    private TraineeService traineeService;

    private TraineeDTO traineeDTO;
    private Trainee createdTrainee;
    private Optional<Trainee> retrievedTrainee;
    private boolean isCreated;
    private Credentials credentials;

    @Given("I have a trainee's details")
    public void i_have_a_trainee_s_details() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setIsActive(true);

        traineeDTO = new TraineeDTO();
        traineeDTO.setUser(userDTO);
        traineeDTO.setAddress("123 Main St");
        traineeDTO.setDateOfBirth(new java.util.Date());
    }

    @When("I create a new trainee")
    public void i_create_a_new_trainee() {
        try {
            createdTrainee = traineeService.createTrainee(traineeDTO);
            isCreated = true;
        } catch (Exception e) {
            isCreated = false;
        }
    }

    @Then("the trainee should be created successfully")
    public void the_trainee_should_be_created_successfully() {
        assertTrue(isCreated);
        assertNotNull(createdTrainee);
        assertEquals("John", createdTrainee.getUser().getFirstName());
        assertEquals("Doe", createdTrainee.getUser().getLastName());
    }

    @Given("I have an existing trainee ID")
    public void i_have_an_existing_trainee_id() {
        credentials = new Credentials("John.Doe", "0123456789");
        createdTrainee = traineeService.getTraineeByUsername("John.Doe", credentials).orElse(null);
        assertNotNull(createdTrainee);
    }

    @When("I retrieve the trainee by ID")
    public void i_retrieve_the_trainee_by_id() {
        if (createdTrainee != null) {
            retrievedTrainee = traineeService.getTraineeById(createdTrainee.getId(), credentials);
        }
    }

    @Then("the correct trainee should be returned")
    public void the_correct_trainee_should_be_returned() {
        assertTrue(retrievedTrainee.isPresent());
        assertEquals(createdTrainee.getId(), retrievedTrainee.get().getId());
    }
}

