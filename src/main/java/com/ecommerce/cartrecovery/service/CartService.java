package com.ecommerce.cartrecovery.service;

import com.ecommerce.cartrecovery.dto.CartActivityEvent;
import com.ecommerce.cartrecovery.entity.Cart;
import com.ecommerce.cartrecovery.entity.CartItem;
import com.ecommerce.cartrecovery.enums.CartActivityType;
import com.ecommerce.cartrecovery.enums.CartStatusEnum;
import com.ecommerce.cartrecovery.exceptions.CartNotFoundException;
import com.ecommerce.cartrecovery.exceptions.ItemNotFoundException;
import com.ecommerce.cartrecovery.kafka.producer.CartUpdateEventProducer;
import com.ecommerce.cartrecovery.repository.CartItemRepository;
import com.ecommerce.cartrecovery.repository.CartRepository;
import com.ecommerce.cartrecovery.request.AddItemRequest;
import com.ecommerce.cartrecovery.request.CartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartUpdateEventProducer cartUpdateEventProducer;

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public Cart createCart(CartRequest cartRequest) {
        Cart cart = new Cart();
        cart.setUserId(cartRequest.getUserId());
        cart.setDeviceId(cartRequest.getDeviceId());
        cart.setStatus(CartStatusEnum.CREATED);
        return cartRepository.save(cart);
    }

    public boolean addItem(AddItemRequest request, BigInteger cartId) {
        Cart cart = cartRepository.findByCartIdAndStatus(cartId, CartStatusEnum.CREATED)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart with id " + cartId + " not found"));

        CartItem cartItem = new CartItem();
        cartItem.setItemId(request.getItemId())
                .setCart(cart);
        cartItemRepository.save(cartItem);

        publishCartActivityEvent(cart.getCartId(), CartActivityType.MODIFY, LocalDateTime.now());
        return true;
    }

    public void checkoutCart(BigInteger cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart with id " + cartId + " not found"));

        cart.setStatus(CartStatusEnum.CHECKED_OUT);
        cartRepository.save(cart);
        publishCartActivityEvent(cart.getCartId(), CartActivityType.CHECKOUT, LocalDateTime.now());

    }

    public Cart getCartById(BigInteger cartId) {
        Optional<Cart> cart =  cartRepository.findById(cartId);
        return cart.orElse(null);
    }

    public Cart viewCart(BigInteger cartId) {
        Optional<Cart> cart =  cartRepository.findById(cartId);
        cart.ifPresent(activeCart -> publishCartActivityEvent(activeCart.getCartId(), CartActivityType.VIEW, LocalDateTime.now()));
        return cart.orElse(null);
    }

    public void deleteCart(BigInteger cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart with id " + cartId + " not found"));
        cartRepository.delete(cart);
        publishCartActivityEvent(cart.getCartId(), CartActivityType.DELETED, LocalDateTime.now());
    }

    public void removeItem(BigInteger cartId, BigInteger itemId) {
        Cart cart = cartRepository.findByCartIdAndStatus(cartId, CartStatusEnum.CREATED)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart with id " + cartId + " not found"));

        CartItem item = cartItemRepository
                .findByCart_CartIdAndItemId(cartId, itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found in cart"));
        cartItemRepository.delete(item);

        // delete cart in case of no items
        if (cart.getItems().isEmpty()) {
            deleteCart(cart.getCartId());
        } else {
            publishCartActivityEvent(cart.getCartId(), CartActivityType.MODIFY, LocalDateTime.now());
        }
    }

    private void publishCartActivityEvent(BigInteger cartId, CartActivityType activityType, LocalDateTime timestamp) {
        CartActivityEvent cartActivityEvent = new CartActivityEvent();
        cartActivityEvent.setCartId(cartId);
        cartActivityEvent.setActivityType(activityType);
        cartActivityEvent.setActivityTimestamp(timestamp);

        cartUpdateEventProducer.publishCartUpdateEvent(cartActivityEvent);
    }
}
