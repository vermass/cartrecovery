package com.ecommerce.cartrecovery.exceptions;

import com.ecommerce.cartrecovery.enums.ErrorCode;

public class CartRecoveryException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String details;

    public CartRecoveryException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public CartRecoveryException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public CartRecoveryException(ErrorCode errorCode, String message, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetails() {
        return details;
    }
}

