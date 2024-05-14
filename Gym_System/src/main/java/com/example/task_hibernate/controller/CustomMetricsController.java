package com.example.task_hibernate.controller;

import com.example.task_hibernate.metrics.CustomMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class CustomMetricsController {
    private final CustomMetrics customMetrics;

    @Autowired
    public CustomMetricsController(CustomMetrics customMetrics) {
        this.customMetrics = customMetrics;
    }

    @GetMapping("/custom")
    public String exampleEndpoint() {
        customMetrics.incrementRequestCount();
        return "custom metric incremented " + customMetrics.requestsTotal.get() + " times";
    }
}
