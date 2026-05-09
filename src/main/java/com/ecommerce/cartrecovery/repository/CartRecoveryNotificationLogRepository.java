package com.ecommerce.cartrecovery.repository;


import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationLogEntity;
import com.ecommerce.cartrecovery.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRecoveryNotificationLogRepository extends JpaRepository<CartRecoveryNotificationLogEntity, BigInteger> {

    List<CartRecoveryNotificationLogEntity>
    findByCartId(BigInteger cartId);

    List<CartRecoveryNotificationLogEntity>
    findByUserId(BigInteger userId);

    List<CartRecoveryNotificationLogEntity>
    findByType(NotificationType type);

    Optional<CartRecoveryNotificationLogEntity>
    findTopByCartIdOrderByTimestampDesc(
            BigInteger cartId
    );
}