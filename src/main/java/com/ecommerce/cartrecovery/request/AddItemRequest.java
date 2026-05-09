package com.ecommerce.cartrecovery.request;


import java.math.BigInteger;

public class AddItemRequest {

    private BigInteger itemId;

    public BigInteger getItemId() {
        return itemId;
    }

    public void setItemId(BigInteger itemId) {
        this.itemId = itemId;
    }
}