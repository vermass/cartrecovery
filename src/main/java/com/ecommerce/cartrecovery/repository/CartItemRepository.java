package com.ecommerce.cartrecovery.repository;

import com.ecommerce.cartrecovery.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, BigInteger> {
    Optional<CartItem> findByCart_CartIdAndItemId(
            BigInteger cartId,
            BigInteger itemId
    );
}
