package com.banking.customer.service.client;

import com.banking.customer.controller.config.exception.ClientAlreadyExistsException;
import com.banking.customer.controller.config.exception.ClientNotFoundException;
import com.banking.customer.entity.Client;
import com.banking.customer.entity.repository.ClientRepository;
import com.banking.customer.service.dto.ClientRequest;
import com.banking.customer.service.dto.ClientResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class ClientServiceTest {

    @Inject
    ClientService clientService;

    @InjectMock
    ClientRepository clientRepository;

    @Test
    void testGetAllClients_ReturnsAllClients() {
        Client client1 = createTestClient(1L, "John Doe", "DOC001", "john@example.com", "ACTIVE");
        Client client2 = createTestClient(2L, "Jane Doe", "DOC002", "jane@example.com", "ACTIVE");

        when(clientRepository.listAll()).thenReturn(Arrays.asList(client1, client2));

        List<ClientResponse> result = clientService.getAll();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
        verify(clientRepository, times(1)).listAll();
    }

    @Test
    void testGetClientByDocumentId_WhenExists_ReturnsClient() {
        Client client = createTestClient(1L, "John Doe", "DOC123", "john@example.com", "ACTIVE");

        when(clientRepository.findByDocumentId("DOC123")).thenReturn(Optional.of(client));

        ClientResponse result = clientService.getClientByDocumentId("DOC123");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("DOC123", result.getDocumentId());
    }

    @Test
    void testGetClientByDocumentId_WhenNotExists_ThrowsException() {
        when(clientRepository.findByDocumentId("NONEXISTENT")).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () ->
            clientService.getClientByDocumentId("NONEXISTENT")
        );
    }

    @Test
    void testCreateClient_WithValidData_CreatesClient() {
        ClientRequest request = ClientRequest.builder()
                .name("New Client")
                .documentId("NEW123")
                .email("new@example.com")
                .status("ACTIVE")
                .build();

        when(clientRepository.existsByDocumentId("NEW123")).thenReturn(false);
        when(clientRepository.existsByEmail("new@example.com")).thenReturn(false);

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        doNothing().when(clientRepository).persist(clientCaptor.capture());

        ClientResponse result = clientService.create(request);

        Client capturedClient = clientCaptor.getValue();
        assertEquals("New Client", capturedClient.getName());
        assertEquals("NEW123", capturedClient.getDocumentId());
        assertEquals("new@example.com", capturedClient.getEmail());
        assertEquals("ACTIVE", capturedClient.getStatus());
    }

    @Test
    void testCreateClient_WithoutStatus_DefaultsToActive() {
        ClientRequest request = ClientRequest.builder()
                .name("New Client")
                .documentId("NEW456")
                .email("new2@example.com")
                .build();

        when(clientRepository.existsByDocumentId("NEW456")).thenReturn(false);
        when(clientRepository.existsByEmail("new2@example.com")).thenReturn(false);

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        doNothing().when(clientRepository).persist(clientCaptor.capture());

        clientService.create(request);

        Client capturedClient = clientCaptor.getValue();
        assertEquals("ACTIVE", capturedClient.getStatus());
    }

    @Test
    void testCreateClient_WithDuplicateDocumentId_ThrowsException() {
        ClientRequest request = ClientRequest.builder()
                .name("Duplicate Client")
                .documentId("DUP123")
                .email("duplicate@example.com")
                .build();

        when(clientRepository.existsByDocumentId("DUP123")).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class, () ->
            clientService.create(request)
        );

        verify(clientRepository, never()).persist(any(Client.class));
    }

    @Test
    void testCreateClient_WithDuplicateEmail_ThrowsException() {
        ClientRequest request = ClientRequest.builder()
                .name("Duplicate Email Client")
                .documentId("DOC789")
                .email("duplicate@example.com")
                .build();

        when(clientRepository.existsByDocumentId("DOC789")).thenReturn(false);
        when(clientRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class, () ->
            clientService.create(request)
        );

        verify(clientRepository, never()).persist(any(Client.class));
    }

    @Test
    void testUpdateClient_WithValidData_UpdatesClient() {
        Client existingClient = createTestClient(1L, "Old Name", "DOC123", "old@example.com", "ACTIVE");

        ClientRequest request = ClientRequest.builder()
                .name("Updated Name")
                .documentId("DOC123")
                .email("old@example.com")
                .status("ACTIVE")
                .build();

        when(clientRepository.findByIdOptional(1L)).thenReturn(Optional.of(existingClient));

        ClientResponse result = clientService.update(1L, request);

        assertEquals("Updated Name", existingClient.getName());
        verify(clientRepository, times(1)).persist(existingClient);
    }

    @Test
    void testUpdateClient_WhenNotExists_ThrowsException() {
        ClientRequest request = ClientRequest.builder()
                .name("Updated Name")
                .documentId("DOC123")
                .email("updated@example.com")
                .build();

        when(clientRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () ->
            clientService.update(999L, request)
        );
    }

    @Test
    void testUpdateClient_WithNewDocumentId_ValidatesUniqueness() {
        Client existingClient = createTestClient(1L, "Client", "OLD123", "client@example.com", "ACTIVE");

        ClientRequest request = ClientRequest.builder()
                .name("Client")
                .documentId("NEW123")
                .email("client@example.com")
                .build();

        when(clientRepository.findByIdOptional(1L)).thenReturn(Optional.of(existingClient));
        when(clientRepository.existsByDocumentId("NEW123")).thenReturn(false);

        clientService.update(1L, request);

        assertEquals("NEW123", existingClient.getDocumentId());
        verify(clientRepository).existsByDocumentId("NEW123");
    }

    @Test
    void testUpdateClient_WithDuplicateDocumentId_ThrowsException() {
        Client existingClient = createTestClient(1L, "Client", "OLD123", "client@example.com", "ACTIVE");

        ClientRequest request = ClientRequest.builder()
                .name("Client")
                .documentId("DUP123")
                .email("client@example.com")
                .build();

        when(clientRepository.findByIdOptional(1L)).thenReturn(Optional.of(existingClient));
        when(clientRepository.existsByDocumentId("DUP123")).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class, () ->
            clientService.update(1L, request)
        );
    }

    @Test
    void testUpdateClient_WithNewEmail_ValidatesUniqueness() {
        Client existingClient = createTestClient(1L, "Client", "DOC123", "old@example.com", "ACTIVE");

        ClientRequest request = ClientRequest.builder()
                .name("Client")
                .documentId("DOC123")
                .email("new@example.com")
                .build();

        when(clientRepository.findByIdOptional(1L)).thenReturn(Optional.of(existingClient));
        when(clientRepository.existsByEmail("new@example.com")).thenReturn(false);

        clientService.update(1L, request);

        assertEquals("new@example.com", existingClient.getEmail());
        verify(clientRepository).existsByEmail("new@example.com");
    }

    @Test
    void testUpdateClient_WithDuplicateEmail_ThrowsException() {
        Client existingClient = createTestClient(1L, "Client", "DOC123", "old@example.com", "ACTIVE");

        ClientRequest request = ClientRequest.builder()
                .name("Client")
                .documentId("DOC123")
                .email("duplicate@example.com")
                .build();

        when(clientRepository.findByIdOptional(1L)).thenReturn(Optional.of(existingClient));
        when(clientRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class, () ->
            clientService.update(1L, request)
        );
    }

    @Test
    void testDeleteClient_WhenExists_SoftDeletesClient() {
        Client client = createTestClient(1L, "Client", "DOC123", "client@example.com", "ACTIVE");

        when(clientRepository.findByIdOptional(1L)).thenReturn(Optional.of(client));

        clientService.delete(1L);

        assertEquals("INACTIVE", client.getStatus());
        verify(clientRepository, times(1)).persist(client);
    }

    @Test
    void testDeleteClient_WhenNotExists_ThrowsException() {
        when(clientRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () ->
            clientService.delete(999L)
        );
    }

    @Test
    void testCountActiveClients() {
        when(clientRepository.countActiveClients()).thenReturn(5L);

        long count = clientService.countActive();

        assertEquals(5L, count);
        verify(clientRepository, times(1)).countActiveClients();
    }

    private Client createTestClient(Long id, String name, String documentId, String email, String status) {
        return Client.builder()
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
