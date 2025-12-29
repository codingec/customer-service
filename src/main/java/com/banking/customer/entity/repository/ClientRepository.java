package com.banking.customer.entity.repository;

import com.banking.customer.entity.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class ClientRepository implements PanacheRepository<Client> {

    /**
     * Busca un cliente por su documento de identidad
     */
    public Optional<Client> findByDocumentId(String documentId) {
        return find("documentId", documentId).firstResultOptional();
    }


    /**
     * Cuenta clientes activos
     */
    public long countActiveClients() {
        return count("status", "ACTIVE");
    }

    /**
     * Verifica si existe un cliente con el documento dado
     */
    public boolean existsByDocumentId(String documentId) {
        return count("documentId", documentId) > 0;
    }

    /**
     * Verifica si existe un cliente con el email dado
     */
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
}
