package com.banking.customer.controller.config.exception.mapper;

import com.banking.customer.service.dto.ErrorResponse;
import com.banking.customer.controller.config.exception.ClientAlreadyExistsException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class ClientAlreadyExistsExceptionMapper implements ExceptionMapper<ClientAlreadyExistsException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ClientAlreadyExistsException exception) {
        log.error("Cliente duplicado: {}", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                Response.Status.CONFLICT.getStatusCode(),
                "Conflict",
                exception.getMessage(),
                uriInfo.getPath()
        );

        return Response.status(Response.Status.CONFLICT)
                .entity(errorResponse)
                .build();
    }
}
