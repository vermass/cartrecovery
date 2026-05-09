package com.ecommerce.cartrecovery.repository;

import com.ecommerce.cartrecovery.entity.CartRecoveryNotificationConfigEntity;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface CartRecoveryNotificationConfigRepository extends JpaRepository<CartRecoveryNotificationConfigEntity, BigInteger> {

    Optional<CartRecoveryNotificationConfigEntity> findFirstByStage(Integer stage);
    Optional<CartRecoveryNotificationConfigEntity> findFirstByStageAndType(Integer stage, String type);
}