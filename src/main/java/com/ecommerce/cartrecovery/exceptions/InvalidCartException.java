package com.ecommerce.cartrecovery.exceptions;

public class InvalidCartException extends RuntimeException {
    public InvalidCartException(String message) {
        super(message);
    }
}
