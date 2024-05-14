package com.example.task_hibernate.metrics;

import org.springframework.stereotype.Component;
import io.prometheus.client.Counter;

@Component
public class CustomMetrics {
    public final Counter requestsTotal = Counter.build()
            .name("myapp_requests_total")
            .help("Total number of requests")
            .register();

    public void incrementRequestCount() {
        requestsTotal.inc();
    }
}
