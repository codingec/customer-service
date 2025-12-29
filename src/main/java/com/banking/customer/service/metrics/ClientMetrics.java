package com.banking.customer.service.metrics;

import com.banking.customer.entity.repository.ClientRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class ClientMetrics {

    @Inject
    ClientRepository clientRepository;

    @Inject
    MeterRegistry meterRegistry;

    @jakarta.annotation.PostConstruct
    void registerMetrics() {
        log.info("Registrando métricas personalizadas de clientes");

        // Gauge para el total de clientes
        Gauge.builder("clients.total", clientRepository, ClientRepository::count)
                .description("Número total de clientes en el sistema")
                .tag("service", "customer-service")
                .register(meterRegistry);

        // Gauge para clientes activos
        Gauge.builder("clients.active", clientRepository, ClientRepository::countActiveClients)
                .description("Número de clientes activos")
                .tag("service", "customer-service")
                .tag("status", "active")
                .register(meterRegistry);

        // Gauge para clientes inactivos
        Gauge.builder("clients.inactive", clientRepository,
                repo -> repo.count() - repo.countActiveClients())
                .description("Número de clientes inactivos")
                .tag("service", "customer-service")
                .tag("status", "inactive")
                .register(meterRegistry);

        log.info("Métricas personalizadas registradas exitosamente");
    }
}
