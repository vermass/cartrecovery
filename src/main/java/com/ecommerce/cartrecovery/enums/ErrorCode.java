package com.ecommerce.cartrecovery.enums;

public enum ErrorCode {
    CART_NOT_FOUND("CART_NOT_FOUND", "Cart not found"),
    ITEM_NOT_FOUND("ITEM_NOT_FOUND", "Item not found in cart"),
    CAMPAIGN_NOT_FOUND("CAMPAIGN_NOT_FOUND", "Campaign not found"),
    CONFIG_NOT_FOUND("CONFIG_NOT_FOUND", "Configuration not found"),
    BAD_REQUEST("BAD_REQUEST", "Bad request");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

