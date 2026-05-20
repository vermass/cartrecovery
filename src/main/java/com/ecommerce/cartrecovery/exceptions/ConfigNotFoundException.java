package com.ecommerce.cartrecovery.exceptions;

import com.ecommerce.cartrecovery.enums.ErrorCode;

public class ConfigNotFoundException extends CartRecoveryException {
    public ConfigNotFoundException(String message) {
        super(ErrorCode.CONFIG_NOT_FOUND, message);
    }

    public ConfigNotFoundException() {
        super(ErrorCode.CONFIG_NOT_FOUND);
    }
}

