package com.banking.customer.entity.repository;

import com.banking.customer.entity.Client;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ClientRepositoryTest {

    @Inject
    ClientRepository clientRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up database before each test
        clientRepository.deleteAll();
    }

    @Test
    @Transactional
    void testPersistAndFindClient() {
        Client client = Client.builder()
                .name("John Doe")
                .documentId("12345678")
                .email("john.doe@example.com")
                .status("ACTIVE")
                .build();

        clientRepository.persist(client);

        assertNotNull(client.getId());

        Client found = clientRepository.findById(client.getId());
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
        assertEquals("12345678", found.getDocumentId());
        assertEquals("john.doe@example.com", found.getEmail());
        assertEquals("ACTIVE", found.getStatus());
    }

    @Test
    @Transactional
    void testFindByDocumentId_WhenExists_ReturnsClient() {
        Client client = Client.builder()
                .name("Jane Doe")
                .documentId("87654321")
                .email("jane.doe@example.com")
                .status("ACTIVE")
                .build();

        clientRepository.persist(client);

        Optional<Client> found = clientRepository.findByDocumentId("87654321");

        assertTrue(found.isPresent());
        assertEquals("Jane Doe", found.get().getName());
        assertEquals("87654321", found.get().getDocumentId());
    }

    @Test
    @Transactional
    void testFindByDocumentId_WhenNotExists_ReturnsEmpty() {
        Optional<Client> found = clientRepository.findByDocumentId("NONEXISTENT");

        assertFalse(found.isPresent());
    }

    @Test
    @Transactional
    void testCountActiveClients_WithMultipleClients() {
        Client active1 = Client.builder()
                .name("Active Client 1")
                .documentId("DOC001")
                .email("active1@example.com")
                .status("ACTIVE")
                .build();

        Client active2 = Client.builder()
                .name("Active Client 2")
                .documentId("DOC002")
                .email("active2@example.com")
                .status("ACTIVE")
                .build();

        Client inactive = Client.builder()
                .name("Inactive Client")
                .documentId("DOC003")
                .email("inactive@example.com")
                .status("INACTIVE")
                .build();

        Client blocked = Client.builder()
                .name("Blocked Client")
                .documentId("DOC004")
                .email("blocked@example.com")
                .status("BLOCKED")
                .build();

        clientRepository.persist(active1);
        clientRepository.persist(active2);
        clientRepository.persist(inactive);
        clientRepository.persist(blocked);

        long activeCount = clientRepository.countActiveClients();

        assertEquals(2, activeCount);
    }

    @Test
    @Transactional
    void testCountActiveClients_WhenNoActiveClients_ReturnsZero() {
        Client inactive = Client.builder()
                .name("Inactive Client")
                .documentId("DOC001")
                .email("inactive@example.com")
                .status("INACTIVE")
                .build();

        clientRepository.persist(inactive);

        long activeCount = clientRepository.countActiveClients();

        assertEquals(0, activeCount);
    }

    @Test
    @Transactional
    void testExistsByDocumentId_WhenExists_ReturnsTrue() {
        Client client = Client.builder()
                .name("Test Client")
                .documentId("TEST123")
                .email("test@example.com")
                .status("ACTIVE")
                .build();

        clientRepository.persist(client);

        boolean exists = clientRepository.existsByDocumentId("TEST123");

        assertTrue(exists);
    }

    @Test
    @Transactional
    void testExistsByDocumentId_WhenNotExists_ReturnsFalse() {
        boolean exists = clientRepository.existsByDocumentId("NONEXISTENT");

        assertFalse(exists);
    }

    @Test
    @Transactional
    void testExistsByEmail_WhenExists_ReturnsTrue() {
        Client client = Client.builder()
                .name("Test Client")
                .documentId("TEST456")
                .email("unique@example.com")
                .status("ACTIVE")
                .build();

        clientRepository.persist(client);

        boolean exists = clientRepository.existsByEmail("unique@example.com");

        assertTrue(exists);
    }

    @Test
    @Transactional
    void testExistsByEmail_WhenNotExists_ReturnsFalse() {
        boolean exists = clientRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    @Transactional
    void testListAll() {
        Client client1 = Client.builder()
                .name("Client 1")
                .documentId("DOC001")
                .email("client1@example.com")
                .status("ACTIVE")
                .build();

        Client client2 = Client.builder()
                .name("Client 2")
                .documentId("DOC002")
                .email("client2@example.com")
                .status("INACTIVE")
                .build();

        clientRepository.persist(client1);
        clientRepository.persist(client2);

        var clients = clientRepository.listAll();

        assertEquals(2, clients.size());
    }

    @Test
    @Transactional
    void testUpdateClient() {
        Client client = Client.builder()
                .name("Original Name")
                .documentId("DOC123")
                .email("original@example.com")
                .status("ACTIVE")
                .build();

        clientRepository.persist(client);

        client.setName("Updated Name");
        client.setEmail("updated@example.com");
        clientRepository.persist(client);

        Client updated = clientRepository.findById(client.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    @Transactional
    void testDeleteClient() {
        Client client = Client.builder()
                .name("To Delete")
                .documentId("DELETE123")
                .email("delete@example.com")
                .status("ACTIVE")
                .build();

        clientRepository.persist(client);
        Long clientId = client.getId();

        clientRepository.delete(client);

        Client deleted = clientRepository.findById(clientId);
        assertNull(deleted);
    }

    @Test
    @Transactional
    void testCount() {
        Client client1 = Client.builder()
                .name("Client 1")
                .documentId("DOC001")
                .email("client1@example.com")
                .status("ACTIVE")
                .build();

        Client client2 = Client.builder()
                .name("Client 2")
                .documentId("DOC002")
                .email("client2@example.com")
                .status("ACTIVE")
                .build();

        clientRepository.persist(client1);
        clientRepository.persist(client2);

        long count = clientRepository.count();

        assertEquals(2, count);
    }
}
