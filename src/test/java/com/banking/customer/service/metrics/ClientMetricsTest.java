package com.banking.customer.service.metrics;

import com.banking.customer.entity.repository.ClientRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ClientMetricsTest {

    @Inject
    ClientMetrics clientMetrics;

    @Inject
    ClientRepository clientRepository;

    @Inject
    MeterRegistry meterRegistry;

    @Test
    void testClientMetricsBeanIsCreated() {
        assertNotNull(clientMetrics, "ClientMetrics bean should be created");
    }

    @Test
    void testClientRepositoryIsInjected() {
        assertNotNull(clientRepository, "ClientRepository should be injected");
    }

    @Test
    void testMeterRegistryIsInjected() {
        assertNotNull(meterRegistry, "MeterRegistry should be injected");
    }

    @Test
    void testClientRepositoryCountMethod() {
        // Test that the repository count method works
        long count = clientRepository.count();
        assertTrue(count >= 0, "Client count should be non-negative");
    }

    @Test
    void testClientRepositoryActiveClientsMethod() {
        // Test that the repository active clients method works
        long activeCount = clientRepository.countActiveClients();
        assertTrue(activeCount >= 0, "Active client count should be non-negative");
    }
}
