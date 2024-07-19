package com.example.trainer_workload.stepdefs;

import com.example.trainer_workload.config.CucumberSpringConfiguration;
import com.example.trainer_workload.model.WorkloadRequest;
import com.example.trainer_workload.service.TrainerService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
@SpringBootTest(classes = CucumberSpringConfiguration.class)
public class DLQStepDefs {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private JmsTemplate jmsTemplate;

    private WorkloadRequest workloadRequest;

    @Given("a workload request with username {string} that will fail")
    public void a_workload_request_with_username_that_will_fail(String username) {
        workloadRequest = new WorkloadRequest(username, "FirstName", "LastName", true, LocalDate.now(), 60, "INVALID_ACTION");
    }

    @When("the workload request is processed")
    public void the_workload_request_is_processed() {
        jmsTemplate.convertAndSend("workload.queue", workloadRequest);
    }

    @Then("the request should be sent to the DLQ")
    public void the_request_should_be_sent_to_the_dlq() {
        boolean messageInDLQ = jmsTemplate.receiveAndConvert("DLQ") != null;
        assertTrue(messageInDLQ);
    }
}

