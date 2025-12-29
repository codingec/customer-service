package com.banking.customer.controller.config.exception.mapper;

import com.banking.customer.service.dto.ErrorResponse;
import com.banking.customer.controller.config.exception.ClientNotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class ClientNotFoundExceptionMapper implements ExceptionMapper<ClientNotFoundException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ClientNotFoundException exception) {
        log.error("Cliente no encontrado: {}", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                Response.Status.NOT_FOUND.getStatusCode(),
                "Not Found",
                exception.getMessage(),
                uriInfo.getPath()
        );

        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorResponse)
                .build();
    }
}
