package com.example.task_hibernate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TaskHibernateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskHibernateApplication.class, args);
    }
}
