package com.banking.customer.controller.config.exception.mapper;

import com.banking.customer.service.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Provider
@Slf4j
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        log.error("Error de validación: {}", exception.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            validationErrors.put(propertyPath, message);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(Response.Status.BAD_REQUEST.getStatusCode())
                .error("Validation Error")
                .message("Error en la validación de datos")
                .path(uriInfo.getPath())
                .validationErrors(validationErrors)
                .build();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
}
