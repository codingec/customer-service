package com.banking.customer.controller.config.health;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ServiceHealthCheckTest {

    @Inject
    @Liveness
    ServiceHealthCheck serviceHealthCheck;

    @Test
    void testCall_ReturnsUp() {
        HealthCheckResponse response = serviceHealthCheck.call();

        assertNotNull(response);
        assertEquals("Customer Service", response.getName());
        assertEquals(HealthCheckResponse.Status.UP, response.getStatus());
    }

    @Test
    void testCall_ContainsServiceData() {
        HealthCheckResponse response = serviceHealthCheck.call();

        assertTrue(response.getData().isPresent());
        assertEquals("customer-service", response.getData().get().get("service"));
    }

    @Test
    void testCall_ContainsVersionData() {
        HealthCheckResponse response = serviceHealthCheck.call();

        assertTrue(response.getData().isPresent());
        assertEquals("1.0.0", response.getData().get().get("version"));
    }

    @Test
    void testCall_ContainsStatusData() {
        HealthCheckResponse response = serviceHealthCheck.call();

        assertTrue(response.getData().isPresent());
        assertEquals("running", response.getData().get().get("status"));
    }

    @Test
    void testCall_AlwaysReturnsConsistentData() {
        HealthCheckResponse response1 = serviceHealthCheck.call();
        HealthCheckResponse response2 = serviceHealthCheck.call();

        assertEquals(response1.getName(), response2.getName());
        assertEquals(response1.getStatus(), response2.getStatus());
        assertEquals(response1.getData().get().get("service"), response2.getData().get().get("service"));
        assertEquals(response1.getData().get().get("version"), response2.getData().get().get("version"));
        assertEquals(response1.getData().get().get("status"), response2.getData().get().get("status"));
    }
}
