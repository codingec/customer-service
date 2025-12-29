package com.banking.customer.controller.config.exception.mapper;

import com.banking.customer.service.dto.ErrorResponse;
import com.banking.customer.controller.config.exception.InvalidClientStatusException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class InvalidClientStatusExceptionMapper implements ExceptionMapper<InvalidClientStatusException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(InvalidClientStatusException exception) {
        log.error("Estado de cliente inválido: {}", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Bad Request",
                exception.getMessage(),
                uriInfo.getPath()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
}
