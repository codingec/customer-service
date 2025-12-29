package com.banking.customer.service.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClientRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidClientRequest() {
        ClientRequest request = ClientRequest.builder()
                .name("John Doe")
                .documentId("12345678")
                .email("john.doe@example.com")
                .status("ACTIVE")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidClientRequestWithoutStatus() {
        ClientRequest request = ClientRequest.builder()
                .name("Jane Doe")
                .documentId("87654321")
                .email("jane.doe@example.com")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidClientRequest_BlankName() {
        ClientRequest request = ClientRequest.builder()
                .name("")
                .documentId("12345678")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testInvalidClientRequest_NullName() {
        ClientRequest request = ClientRequest.builder()
                .documentId("12345678")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testInvalidClientRequest_NameTooShort() {
        ClientRequest request = ClientRequest.builder()
                .name("A")
                .documentId("12345678")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testInvalidClientRequest_NameTooLong() {
        String longName = "A".repeat(101);
        ClientRequest request = ClientRequest.builder()
                .name(longName)
                .documentId("12345678")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testInvalidClientRequest_BlankDocumentId() {
        ClientRequest request = ClientRequest.builder()
                .name("John Doe")
                .documentId("")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("documentId")));
    }

    @Test
    void testInvalidClientRequest_DocumentIdTooShort() {
        ClientRequest request = ClientRequest.builder()
                .name("John Doe")
                .documentId("1234")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("documentId")));
    }

    @Test
    void testInvalidClientRequest_DocumentIdTooLong() {
        String longDocumentId = "1".repeat(21);
        ClientRequest request = ClientRequest.builder()
                .name("John Doe")
                .documentId(longDocumentId)
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("documentId")));
    }

    @Test
    void testInvalidClientRequest_BlankEmail() {
        ClientRequest request = ClientRequest.builder()
                .name("John Doe")
                .documentId("12345678")
                .email("")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testInvalidClientRequest_InvalidEmailFormat() {
        ClientRequest request = ClientRequest.builder()
                .name("John Doe")
                .documentId("12345678")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testGettersAndSetters() {
        ClientRequest request = new ClientRequest();
        request.setName("Test Name");
        request.setDocumentId("TEST123");
        request.setEmail("test@example.com");
        request.setStatus("ACTIVE");

        assertEquals("Test Name", request.getName());
        assertEquals("TEST123", request.getDocumentId());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("ACTIVE", request.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        ClientRequest request = new ClientRequest();
        assertNotNull(request);
    }

    @Test
    void testAllArgsConstructor() {
        ClientRequest request = new ClientRequest("John Doe", "DOC123", "john@example.com", "ACTIVE");

        assertEquals("John Doe", request.getName());
        assertEquals("DOC123", request.getDocumentId());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("ACTIVE", request.getStatus());
    }

    @Test
    void testBuilder() {
        ClientRequest request = ClientRequest.builder()
                .name("Builder Test")
                .documentId("BUILD123")
                .email("builder@test.com")
                .status("INACTIVE")
                .build();

        assertEquals("Builder Test", request.getName());
        assertEquals("BUILD123", request.getDocumentId());
        assertEquals("builder@test.com", request.getEmail());
        assertEquals("INACTIVE", request.getStatus());
    }
}
