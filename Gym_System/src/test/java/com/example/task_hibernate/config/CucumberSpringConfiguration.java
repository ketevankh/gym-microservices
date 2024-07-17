package com.example.task_hibernate.config;

import com.example.task_hibernate.TaskHibernateApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = TaskHibernateApplication.class)
@SpringBootTest(classes = TaskHibernateApplication.class)
@ComponentScan(basePackages = "com.example.task_hibernate")
public class CucumberSpringConfiguration {
}
