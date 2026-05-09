package com.ecommerce.cartrecovery.entity;

import com.ecommerce.cartrecovery.enums.CartActivityType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_activity_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartActivityLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "cart_id", nullable = false)
    private BigInteger cartId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private CartActivityType activityType;

    @Column(name = "activity_timestamp", nullable = false)
    private LocalDateTime activityTimestamp;
}