package com.example.task_hibernate.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public Health health() {
        if (databaseIsUp()) {
            return Health.up().build();
        } else {
            return Health.down().build();
        }
    }

    private boolean databaseIsUp() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

