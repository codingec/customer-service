package com.banking.customer.controller.client;

import com.banking.customer.service.client.ClientService;
import com.banking.customer.service.dto.ClientRequest;
import com.banking.customer.service.dto.ClientResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/api/v1/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class ClientController {

    @Inject
    ClientService clientService;

    @Inject
    JsonWebToken jwt;

    /**
     * GET /api/v1/clients
     * Obtiene todos los clientes
     */
    @GET
    @RolesAllowed({"USER", "ADMIN"})
    public Response getAll() {
        log.info("GET /api/v1/clients - Usuario: {}", jwt.getName());
        List<ClientResponse> clients = clientService.getAll();
        return Response.ok(clients).build();
    }

    /**
     * GET /api/v1/clients/document/{documentId}
     * Busca un cliente por documento de identidad
     */
    @GET
    @Path("/document/{documentId}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response getClientByDocumentId(@PathParam("documentId") String documentId) {
        log.info("GET /api/v1/clients/document/{} - Usuario: {}", documentId, jwt.getName());
        ClientResponse client = clientService.getClientByDocumentId(documentId);
        return Response.ok(client).build();
    }

    /**
     * POST /api/v1/clients
     * Crea un nuevo cliente
     */
    @POST
    @RolesAllowed("ADMIN")
    public Response create(@Valid ClientRequest request) {
        log.info("POST /api/v1/clients - Usuario: {}", jwt.getName());
        ClientResponse client = clientService.create(request);
        return Response.status(Response.Status.CREATED).entity(client).build();
    }

    /**
     * PUT /api/v1/clients/{id}
     * Actualiza un cliente existente
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response update(@PathParam("id") Long id, @Valid ClientRequest request) {
        log.info("PUT /api/v1/clients/{} - Usuario: {}", id, jwt.getName());
        ClientResponse client = clientService.update(id, request);
        return Response.ok(client).build();
    }

    /**
     * DELETE /api/v1/clients/{id}
     * Elimina (inactiva) un cliente
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE /api/v1/clients/{} - Usuario: {}", id, jwt.getName());
        clientService.delete(id);
        return Response.noContent().build();
    }

}
