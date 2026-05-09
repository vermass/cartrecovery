package com.ecommerce.cartrecovery.entity;


import com.ecommerce.cartrecovery.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_recovery_notification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRecoveryNotificationLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "cart_id", nullable = false)
    private BigInteger cartId;

    @Column(name = "user_id")
    private BigInteger userId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "stage")
    private Integer stage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime timestamp;
}