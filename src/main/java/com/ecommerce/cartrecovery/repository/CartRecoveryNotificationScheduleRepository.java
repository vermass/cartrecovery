package com.ecommerce.cartrecovery.repository;

import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationScheduleEntity;
import com.ecommerce.cartrecovery.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public interface CartRecoveryNotificationScheduleRepository extends JpaRepository<CartRecoveryNotificationScheduleEntity, BigInteger> {
    List<CartRecoveryNotificationScheduleEntity> findByStatusAndNextScheduleBetween(ScheduleStatus status, LocalDateTime start, LocalDateTime end);
}
