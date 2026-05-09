package com.ecommerce.cartrecovery.repository;

import com.ecommerce.cartrecovery.entity.Cart;
import com.ecommerce.cartrecovery.enums.CartStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, BigInteger> {
    Optional<Cart> findByCartIdAndStatus(BigInteger cartId, CartStatusEnum status);
}
