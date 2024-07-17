package com.example.trainer_workload.config;

import com.example.trainer_workload.TrainerWorkloadApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = TrainerWorkloadApplication.class)
@SpringBootTest(classes = TrainerWorkloadApplication.class)
@ComponentScan(basePackages = "com.example.trainer_workload")
public class CucumberSpringConfiguration {
}
