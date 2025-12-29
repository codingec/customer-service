package com.banking.customer.service.client;

import com.banking.customer.service.dto.ClientRequest;
import com.banking.customer.service.dto.ClientResponse;
import com.banking.customer.entity.Client;
import com.banking.customer.controller.config.exception.ClientAlreadyExistsException;
import com.banking.customer.controller.config.exception.ClientNotFoundException;
import com.banking.customer.controller.config.exception.InvalidClientStatusException;
import com.banking.customer.entity.repository.ClientRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class ClientService {

    @Inject
    ClientRepository clientRepository;

    @Inject
    MeterRegistry meterRegistry;

    private Counter getClientCounter;
    private Counter createClientCounter;
    private Counter updateClientCounter;

    @jakarta.annotation.PostConstruct
    void initMetrics() {
        getClientCounter = meterRegistry.counter("clients.get.count");
        createClientCounter = meterRegistry.counter("clients.create.count");
        updateClientCounter = meterRegistry.counter("clients.update.count");
    }

    /**
    * Obtiene todos los clientes
    */
    public List<ClientResponse> getAll() {
        log.info("Consultando todos los clientes");
        return clientRepository.listAll().stream()
                .map(ClientResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
    * Busca un cliente por documento de identidad
    */
    public ClientResponse getClientByDocumentId(String documentId) {
        log.info("Consultando cliente con documento: {}", documentId);
        getClientCounter.increment();

        Client client = clientRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con documento: " + documentId));

        return ClientResponse.fromEntity(client);
    }

    /**
     * Crea un nuevo cliente
     */
    @Transactional
    public ClientResponse create(ClientRequest request) {
        log.info("Creando nuevo cliente con documento: {}", request.getDocumentId());

        // Validar que no exista
        if (clientRepository.existsByDocumentId(request.getDocumentId())) {
            throw new ClientAlreadyExistsException("Ya existe un cliente con el documento: " + request.getDocumentId());
        }

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new ClientAlreadyExistsException("Ya existe un cliente con el email: " + request.getEmail());
        }

        // Crear cliente
        Client client = Client.builder()
                .name(request.getName())
                .documentId(request.getDocumentId())
                .email(request.getEmail())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();

        clientRepository.persist(client);
        createClientCounter.increment();

        log.info("Cliente creado exitosamente con ID: {}", client.getId());
        return ClientResponse.fromEntity(client);
    }

    /**
     * Actualiza un cliente existente
     */
    @Transactional
    public ClientResponse update(Long id, ClientRequest request) {
        log.info("Actualizando cliente con ID: {}", id);

        Client client = clientRepository.findByIdOptional(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        // Validar documento único (si cambió)
        if (!client.getDocumentId().equals(request.getDocumentId())) {
            if (clientRepository.existsByDocumentId(request.getDocumentId())) {
                throw new ClientAlreadyExistsException("Ya existe un cliente con el documento: " + request.getDocumentId());
            }
            client.setDocumentId(request.getDocumentId());
        }

        if (!client.getEmail().equals(request.getEmail())) {
            if (clientRepository.existsByEmail(request.getEmail())) {
                throw new ClientAlreadyExistsException("Ya existe un cliente con el email: " + request.getEmail());
            }
            client.setEmail(request.getEmail());
        }

        client.setName(request.getName());
        if (request.getStatus() != null) {
            client.setStatus(request.getStatus());
        }

        clientRepository.persist(client);
        updateClientCounter.increment();

        log.info("Cliente actualizado exitosamente: {}", id);
        return ClientResponse.fromEntity(client);
    }

    /**
    * Elimina un cliente (soft delete - cambia a INACTIVE)
    */
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando cliente con ID: {}", id);

        Client client = clientRepository.findByIdOptional(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        client.deactivate();
        clientRepository.persist(client);

        log.info("Cliente eliminado (inactivado) exitosamente: {}", id);
    }


    /**
     * Cuenta clientes activos
     */
    public long countActive() {
        return clientRepository.countActiveClients();
    }
}
