package com.banking.customer.controller.config.health;

import com.banking.customer.entity.repository.ClientRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class DatabaseHealthCheckTest {

    @Inject
    @Readiness
    DatabaseHealthCheck databaseHealthCheck;

    @InjectMock
    ClientRepository clientRepository;

    @Test
    void testCall_WhenDatabaseIsAccessible_ReturnsUp() {
        when(clientRepository.count()).thenReturn(10L);

        HealthCheckResponse response = databaseHealthCheck.call();

        assertNotNull(response);
        assertEquals("Database connection", response.getName());
        assertEquals(HealthCheckResponse.Status.UP, response.getStatus());
        assertTrue(response.getData().isPresent());
        assertEquals(10L, response.getData().get().get("clients_count"));
    }

    @Test
    void testCall_WhenDatabaseIsNotAccessible_ReturnsDown() {
        when(clientRepository.count()).thenThrow(new RuntimeException("Database connection failed"));

        HealthCheckResponse response = databaseHealthCheck.call();

        assertNotNull(response);
        assertEquals("Database connection", response.getName());
        assertEquals(HealthCheckResponse.Status.DOWN, response.getStatus());
        assertTrue(response.getData().isPresent());
        assertTrue(response.getData().get().containsKey("error"));
        assertEquals("Database connection failed", response.getData().get().get("error"));
    }

    @Test
    void testCall_WithZeroClients_ReturnsUp() {
        when(clientRepository.count()).thenReturn(0L);

        HealthCheckResponse response = databaseHealthCheck.call();

        assertEquals(HealthCheckResponse.Status.UP, response.getStatus());
        assertEquals(0L, response.getData().get().get("clients_count"));
    }

    @Test
    void testCall_WithLargeNumberOfClients_ReturnsUp() {
        when(clientRepository.count()).thenReturn(1000000L);

        HealthCheckResponse response = databaseHealthCheck.call();

        assertEquals(HealthCheckResponse.Status.UP, response.getStatus());
        assertEquals(1000000L, response.getData().get().get("clients_count"));
    }
}
