package com.example.task_hibernate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJms
public class TaskHibernateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskHibernateApplication.class, args);
    }
}
