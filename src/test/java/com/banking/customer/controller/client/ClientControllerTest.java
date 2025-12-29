package com.banking.customer.controller.client;

import com.banking.customer.controller.config.exception.ClientAlreadyExistsException;
import com.banking.customer.controller.config.exception.ClientNotFoundException;
import com.banking.customer.service.client.ClientService;
import com.banking.customer.service.dto.ClientRequest;
import com.banking.customer.service.dto.ClientResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@QuarkusTest
class ClientControllerTest {

    @InjectMock
    ClientService clientService;

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    void testGetAllClients_WithUserRole_ReturnsClients() {
        List<ClientResponse> clients = Arrays.asList(
            createClientResponse(1L, "John Doe", "DOC001", "john@example.com", "ACTIVE"),
            createClientResponse(2L, "Jane Doe", "DOC002", "jane@example.com", "ACTIVE")
        );

        when(clientService.getAll()).thenReturn(clients);

        given()
            .when()
            .get("/api/v1/clients")
            .then()
            .statusCode(200)
            .body("size()", is(2))
            .body("[0].name", equalTo("John Doe"))
            .body("[1].name", equalTo("Jane Doe"));
    }

    @Test
    @TestSecurity(user = "adminUser", roles = {"ADMIN"})
    void testGetAllClients_WithAdminRole_ReturnsClients() {
        List<ClientResponse> clients = Arrays.asList(
            createClientResponse(1L, "Client 1", "DOC001", "client1@example.com", "ACTIVE")
        );

        when(clientService.getAll()).thenReturn(clients);

        given()
            .when()
            .get("/api/v1/clients")
            .then()
            .statusCode(200)
            .body("size()", is(1));
    }

    @Test
    void testGetAllClients_WithoutAuth_Returns401() {
        given()
            .when()
            .get("/api/v1/clients")
            .then()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    void testGetClientByDocumentId_WhenExists_ReturnsClient() {
        ClientResponse client = createClientResponse(1L, "John Doe", "DOC123", "john@example.com", "ACTIVE");

        when(clientService.getClientByDocumentId("DOC123")).thenReturn(client);

        given()
            .when()
            .get("/api/v1/clients/document/DOC123")
            .then()
            .statusCode(200)
            .body("name", equalTo("John Doe"))
            .body("documentId", equalTo("DOC123"))
            .body("email", equalTo("john@example.com"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    void testGetClientByDocumentId_WhenNotExists_Returns404() {
        when(clientService.getClientByDocumentId("NONEXISTENT"))
            .thenThrow(new ClientNotFoundException("Cliente no encontrado"));

        given()
            .when()
            .get("/api/v1/clients/document/NONEXISTENT")
            .then()
            .statusCode(404);
    }

    @Test
    @TestSecurity(user = "adminUser", roles = {"ADMIN"})
    void testCreateClient_WithAdminRole_CreatesClient() {
        ClientRequest request = ClientRequest.builder()
            .name("New Client")
            .documentId("NEW123")
            .email("new@example.com")
            .status("ACTIVE")
            .build();

        ClientResponse response = createClientResponse(1L, "New Client", "NEW123", "new@example.com", "ACTIVE");

        when(clientService.create(any(ClientRequest.class))).thenReturn(response);

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/clients")
            .then()
            .statusCode(201)
            .body("name", equalTo("New Client"))
            .body("documentId", equalTo("NEW123"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    void testCreateClient_WithUserRole_Returns403() {
        ClientRequest request = ClientRequest.builder()
            .name("New Client")
            .documentId("NEW123")
            .email("new@example.com")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/clients")
            .then()
            .statusCode(403);
    }

    @Test
    void testCreateClient_WithoutAuth_Returns401() {
        ClientRequest request = ClientRequest.builder()
            .name("New Client")
            .documentId("NEW123")
            .email("new@example.com")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/clients")
            .then()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "adminUser", roles = {"ADMIN"})
    void testCreateClient_WithDuplicateDocumentId_Returns409() {
        ClientRequest request = ClientRequest.builder()
            .name("Duplicate Client")
            .documentId("DUP123")
            .email("duplicate@example.com")
            .build();

        when(clientService.create(any(ClientRequest.class)))
            .thenThrow(new ClientAlreadyExistsException("Ya existe un cliente con el documento"));

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/clients")
            .then()
            .statusCode(409);
    }

    @Test
    @TestSecurity(user = "adminUser", roles = {"ADMIN"})
    void testUpdateClient_WithAdminRole_UpdatesClient() {
        ClientRequest request = ClientRequest.builder()
            .name("Updated Client")
            .documentId("DOC123")
            .email("updated@example.com")
            .build();

        ClientResponse response = createClientResponse(1L, "Updated Client", "DOC123", "updated@example.com", "ACTIVE");

        when(clientService.update(anyLong(), any(ClientRequest.class))).thenReturn(response);

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .put("/api/v1/clients/1")
            .then()
            .statusCode(200)
            .body("name", equalTo("Updated Client"))
            .body("email", equalTo("updated@example.com"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    void testUpdateClient_WithUserRole_Returns403() {
        ClientRequest request = ClientRequest.builder()
            .name("Updated Client")
            .documentId("DOC123")
            .email("updated@example.com")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .put("/api/v1/clients/1")
            .then()
            .statusCode(403);
    }

    @Test
    @TestSecurity(user = "adminUser", roles = {"ADMIN"})
    void testUpdateClient_WhenNotExists_Returns404() {
        ClientRequest request = ClientRequest.builder()
            .name("Updated Client")
            .documentId("DOC123")
            .email("updated@example.com")
            .build();

        when(clientService.update(anyLong(), any(ClientRequest.class)))
            .thenThrow(new ClientNotFoundException(999L));

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .put("/api/v1/clients/999")
            .then()
            .statusCode(404);
    }

    @Test
    @TestSecurity(user = "adminUser", roles = {"ADMIN"})
    void testDeleteClient_WithAdminRole_DeletesClient() {
        given()
            .when()
            .delete("/api/v1/clients/1")
            .then()
            .statusCode(204);
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    void testDeleteClient_WithUserRole_Returns403() {
        given()
            .when()
            .delete("/api/v1/clients/1")
            .then()
            .statusCode(403);
    }

    @Test
    void testDeleteClient_WithoutAuth_Returns401() {
        given()
            .when()
            .delete("/api/v1/clients/1")
            .then()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "adminUser", roles = {"ADMIN"})
    void testDeleteClient_WhenNotExists_Returns404() {
        doThrow(new ClientNotFoundException(999L))
            .when(clientService).delete(999L);

        given()
            .when()
            .delete("/api/v1/clients/999")
            .then()
            .statusCode(404);
    }

    private ClientResponse createClientResponse(Long id, String name, String documentId, String email, String status) {
        return ClientResponse.builder()
            .id(id)
            .name(name)
            .documentId(documentId)
            .email(email)
            .status(status)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}
