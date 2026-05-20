package com.ecommerce.cartrecovery.exceptions;

import com.ecommerce.cartrecovery.enums.ErrorCode;

public class CartNotFoundException extends CartRecoveryException {
    public CartNotFoundException(String message) {
        super(ErrorCode.CART_NOT_FOUND, message);
    }

    public CartNotFoundException() {
        super(ErrorCode.CART_NOT_FOUND);
    }
}

