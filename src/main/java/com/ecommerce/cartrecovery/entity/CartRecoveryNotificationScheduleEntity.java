package com.ecommerce.cartrecovery.entity;

import com.ecommerce.cartrecovery.enums.ScheduleStatus;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "abandoned_cart_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRecoveryNotificationScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "cart_id", nullable = false)
    private BigInteger cartId;

    @Column(name = "user_id")
    private BigInteger userId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "next_schedule")
    private LocalDateTime nextSchedule;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    @Column(name = "current_stage")
    private Integer currentStage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}