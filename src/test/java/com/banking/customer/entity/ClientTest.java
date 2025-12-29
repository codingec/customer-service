package com.banking.customer.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private Client client;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .name("John Doe")
                .documentId("12345678")
                .email("john.doe@example.com")
                .status("ACTIVE")
                .build();
    }

    @Test
    void testClientBuilder() {
        assertNotNull(client);
        assertEquals(1L, client.getId());
        assertEquals("John Doe", client.getName());
        assertEquals("12345678", client.getDocumentId());
        assertEquals("john.doe@example.com", client.getEmail());
        assertEquals("ACTIVE", client.getStatus());
    }

    @Test
    void testIsActive_WhenActive_ReturnsTrue() {
        client.setStatus("ACTIVE");
        assertTrue(client.isActive());
    }

    @Test
    void testIsActive_WhenInactive_ReturnsFalse() {
        client.setStatus("INACTIVE");
        assertFalse(client.isActive());
    }

    @Test
    void testIsActive_WhenBlocked_ReturnsFalse() {
        client.setStatus("BLOCKED");
        assertFalse(client.isActive());
    }

    @Test
    void testActivate_SetsStatusToActive() {
        client.setStatus("INACTIVE");
        client.activate();
        assertEquals("ACTIVE", client.getStatus());
        assertTrue(client.isActive());
    }

    @Test
    void testDeactivate_SetsStatusToInactive() {
        client.setStatus("ACTIVE");
        client.deactivate();
        assertEquals("INACTIVE", client.getStatus());
        assertFalse(client.isActive());
    }

    @Test
    void testBlock_SetsStatusToBlocked() {
        client.setStatus("ACTIVE");
        client.block();
        assertEquals("BLOCKED", client.getStatus());
        assertFalse(client.isActive());
    }

    @Test
    void testOnCreate_SetsTimestamps() {
        Client newClient = Client.builder()
                .name("Jane Doe")
                .documentId("87654321")
                .email("jane.doe@example.com")
                .build();

        newClient.onCreate();

        assertNotNull(newClient.getCreatedAt());
        assertNotNull(newClient.getUpdatedAt());
        assertEquals("ACTIVE", newClient.getStatus());
    }

    @Test
    void testOnCreate_DefaultsToActiveStatus() {
        Client newClient = Client.builder()
                .name("Jane Doe")
                .documentId("87654321")
                .email("jane.doe@example.com")
                .build();

        newClient.onCreate();

        assertEquals("ACTIVE", newClient.getStatus());
    }

    @Test
    void testOnCreate_PreservesExistingStatus() {
        Client newClient = Client.builder()
                .name("Jane Doe")
                .documentId("87654321")
                .email("jane.doe@example.com")
                .status("BLOCKED")
                .build();

        newClient.onCreate();

        assertEquals("BLOCKED", newClient.getStatus());
    }

    @Test
    void testOnUpdate_UpdatesTimestamp() throws InterruptedException {
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());

        LocalDateTime initialUpdatedAt = client.getUpdatedAt();
        Thread.sleep(10);

        client.onUpdate();

        assertNotNull(client.getUpdatedAt());
        assertTrue(client.getUpdatedAt().isAfter(initialUpdatedAt));
    }

    @Test
    void testGettersAndSetters() {
        Client testClient = new Client();

        testClient.setId(100L);
        testClient.setName("Test Name");
        testClient.setDocumentId("TEST123");
        testClient.setEmail("test@test.com");
        testClient.setStatus("INACTIVE");

        LocalDateTime now = LocalDateTime.now();
        testClient.setCreatedAt(now);
        testClient.setUpdatedAt(now);

        assertEquals(100L, testClient.getId());
        assertEquals("Test Name", testClient.getName());
        assertEquals("TEST123", testClient.getDocumentId());
        assertEquals("test@test.com", testClient.getEmail());
        assertEquals("INACTIVE", testClient.getStatus());
        assertEquals(now, testClient.getCreatedAt());
        assertEquals(now, testClient.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        Client emptyClient = new Client();
        assertNotNull(emptyClient);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Client fullClient = new Client(1L, "Name", "DOC123", "email@test.com", "ACTIVE", now, now);

        assertEquals(1L, fullClient.getId());
        assertEquals("Name", fullClient.getName());
        assertEquals("DOC123", fullClient.getDocumentId());
        assertEquals("email@test.com", fullClient.getEmail());
        assertEquals("ACTIVE", fullClient.getStatus());
        assertEquals(now, fullClient.getCreatedAt());
        assertEquals(now, fullClient.getUpdatedAt());
    }
}
