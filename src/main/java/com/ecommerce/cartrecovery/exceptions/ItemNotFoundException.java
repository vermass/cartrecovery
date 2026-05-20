package com.ecommerce.cartrecovery.exceptions;

import com.ecommerce.cartrecovery.enums.ErrorCode;

public class ItemNotFoundException extends CartRecoveryException {
    public ItemNotFoundException(String message) {
        super(ErrorCode.ITEM_NOT_FOUND, message);
    }

    public ItemNotFoundException() {
        super(ErrorCode.ITEM_NOT_FOUND);
    }
}

