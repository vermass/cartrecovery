package com.ecommerce.cartrecovery.request;


import java.math.BigInteger;

public class CartRequest {

    private BigInteger userId;

    private String deviceId;

    public BigInteger getUserId() {
        return userId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}

