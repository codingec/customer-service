package com.banking.customer.controller.config.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException(Long id) {
        super("Cliente no encontrado con ID: " + id);
    }
}
