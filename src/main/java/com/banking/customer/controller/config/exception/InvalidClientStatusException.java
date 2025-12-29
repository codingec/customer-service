package com.banking.customer.controller.config.exception;

public class InvalidClientStatusException extends RuntimeException {
    public InvalidClientStatusException(String message) {
        super(message);
    }
}
