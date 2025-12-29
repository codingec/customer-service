package com.banking.customer.service.dto;

import com.banking.customer.entity.Client;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClientResponseTest {

    @Test
    void testFromEntity_ConvertsClientToResponse() {
        LocalDateTime now = LocalDateTime.now();

        Client client = Client.builder()
                .id(1L)
                .name("John Doe")
                .documentId("12345678")
                .email("john.doe@example.com")
                .status("ACTIVE")
                .createdAt(now)
                .updatedAt(now)
                .build();

        ClientResponse response = ClientResponse.fromEntity(client);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("12345678", response.getDocumentId());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void testFromEntity_WithInactiveStatus() {
        LocalDateTime now = LocalDateTime.now();

        Client client = Client.builder()
                .id(2L)
                .name("Jane Doe")
                .documentId("87654321")
                .email("jane.doe@example.com")
                .status("INACTIVE")
                .createdAt(now)
                .updatedAt(now)
                .build();

        ClientResponse response = ClientResponse.fromEntity(client);

        assertEquals("INACTIVE", response.getStatus());
    }

    @Test
    void testFromEntity_WithBlockedStatus() {
        LocalDateTime now = LocalDateTime.now();

        Client client = Client.builder()
                .id(3L)
                .name("Bob Smith")
                .documentId("11111111")
                .email("bob.smith@example.com")
                .status("BLOCKED")
                .createdAt(now)
                .updatedAt(now)
                .build();

        ClientResponse response = ClientResponse.fromEntity(client);

        assertEquals("BLOCKED", response.getStatus());
    }

    @Test
    void testGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();

        ClientResponse response = new ClientResponse();
        response.setId(100L);
        response.setName("Test Name");
        response.setDocumentId("TEST123");
        response.setEmail("test@example.com");
        response.setStatus("ACTIVE");
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        assertEquals(100L, response.getId());
        assertEquals("Test Name", response.getName());
        assertEquals("TEST123", response.getDocumentId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        ClientResponse response = new ClientResponse();
        assertNotNull(response);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        ClientResponse response = new ClientResponse(
                1L,
                "John Doe",
                "DOC123",
                "john@example.com",
                "ACTIVE",
                now,
                now
        );

        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("DOC123", response.getDocumentId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();

        ClientResponse response = ClientResponse.builder()
                .id(5L)
                .name("Builder Test")
                .documentId("BUILD123")
                .email("builder@test.com")
                .status("INACTIVE")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(5L, response.getId());
        assertEquals("Builder Test", response.getName());
        assertEquals("BUILD123", response.getDocumentId());
        assertEquals("builder@test.com", response.getEmail());
        assertEquals("INACTIVE", response.getStatus());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void testFromEntity_PreservesTimestamps() {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 1, 2, 15, 30);

        Client client = Client.builder()
                .id(1L)
                .name("John Doe")
                .documentId("12345678")
                .email("john.doe@example.com")
                .status("ACTIVE")
                .createdAt(created)
                .updatedAt(updated)
                .build();

        ClientResponse response = ClientResponse.fromEntity(client);

        assertEquals(created, response.getCreatedAt());
        assertEquals(updated, response.getUpdatedAt());
        assertNotEquals(response.getCreatedAt(), response.getUpdatedAt());
    }

    @Test
    void testFromEntity_WithDifferentDocumentIdFormats() {
        Client client = Client.builder()
                .id(1L)
                .name("Test User")
                .documentId("ABC-123-XYZ")
                .email("test@example.com")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ClientResponse response = ClientResponse.fromEntity(client);

        assertEquals("ABC-123-XYZ", response.getDocumentId());
    }

    @Test
    void testFromEntity_WithSpecialCharactersInName() {
        Client client = Client.builder()
                .id(1L)
                .name("José María O'Connor")
                .documentId("12345678")
                .email("jose@example.com")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ClientResponse response = ClientResponse.fromEntity(client);

        assertEquals("José María O'Connor", response.getName());
    }
}
