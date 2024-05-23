package com.example.trainer_workload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrainerWorkloadApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainerWorkloadApplication.class, args);
	}

}
