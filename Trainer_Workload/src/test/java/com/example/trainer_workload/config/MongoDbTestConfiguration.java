package com.example.trainer_workload.config;


import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ContextConfiguration(initializers = {MongoDbTestConfiguration.Initializer.class})
public class MongoDbTestConfiguration implements BeforeAllCallback {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.10");

    @Override
    public void beforeAll(ExtensionContext context) {
        mongoDBContainer.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl()
            );
            values.applyTo(applicationContext);
        }
    }
}

