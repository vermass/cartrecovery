package com.ecommerce.cartrecovery.dto;

import com.ecommerce.cartrecovery.enums.CartActivityType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
public class CartActivityEvent {
    private BigInteger cartId;
    private CartActivityType activityType;
    private LocalDateTime activityTimestamp;
}