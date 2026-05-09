package com.ecommerce.cartrecovery.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger id;

    @Column(name = "item_id")
    private BigInteger itemId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @CreationTimestamp
    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getItemId() {
        return itemId;
    }

    public CartItem setItemId(BigInteger itemId) {
        this.itemId = itemId;
        return this;
    }

    public CartItem setCart(Cart cart) {
        this.cart = cart;
        return this;
    }
}