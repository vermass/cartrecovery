package com.ecommerce.cartrecovery.repository;

import com.ecommerce.cartrecovery.entity.CartActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface CartActivityLogRepository extends JpaRepository<CartActivityLogEntity, BigInteger> {
    Optional<CartActivityLogEntity> findTopByCartIdOrderByActivityTimestampDesc(BigInteger cartId);
}