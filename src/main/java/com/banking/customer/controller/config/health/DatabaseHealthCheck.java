package com.banking.customer.controller.config.health;

import com.banking.customer.entity.repository.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
@Slf4j
public class DatabaseHealthCheck implements HealthCheck {

    @Inject
    ClientRepository clientRepository;

    @Override
    public HealthCheckResponse call() {
        try {
            // Intenta contar los clientes para verificar la conexión a la DB
            long count = clientRepository.count();
            log.debug("Health check de base de datos exitoso. Clientes en DB: {}", count);

            return HealthCheckResponse.named("Database connection")
                    .up()
                    .withData("clients_count", count)
                    .build();
        } catch (Exception e) {
            log.error("Health check de base de datos falló", e);
            return HealthCheckResponse.named("Database connection")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}
