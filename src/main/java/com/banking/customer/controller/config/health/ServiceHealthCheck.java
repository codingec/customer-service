package com.banking.customer.controller.config.health;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
@Slf4j
public class ServiceHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        log.debug("Health check de servicio ejecutado");

        return HealthCheckResponse.named("Customer Service")
                .up()
                .withData("service", "customer-service")
                .withData("version", "1.0.0")
                .withData("status", "running")
                .build();
    }
}
