package com.ecommerce.cartrecovery.controller;

import com.ecommerce.cartrecovery.entity.Cart;
import com.ecommerce.cartrecovery.request.AddItemRequest;
import com.ecommerce.cartrecovery.request.CartRequest;
import com.ecommerce.cartrecovery.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    // create cart
    @PostMapping
    public ResponseEntity<?> createCart(@RequestBody CartRequest cartRequest) {
        if (cartRequest.getUserId() == null && !StringUtils.hasText(cartRequest.getDeviceId())) {
            return ResponseEntity.badRequest().body("userId or deviceId is empty");
        }
        Cart cart = cartService.createCart(cartRequest);
        return ResponseEntity.ok().body(cart);
    }

    // get cart
    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable BigInteger cartId) {
        return ResponseEntity.ok(cartService.viewCart(cartId));
    }

    // Add item
    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItem(@PathVariable BigInteger cartId, @RequestBody  AddItemRequest request) {
        if (BigInteger.ZERO.equals(request.getItemId())) {
            return ResponseEntity.badRequest().body("Invalid itemId");
        }
        boolean success = cartService.addItem(request, cartId);
        return ResponseEntity.ok().body(success);
    }

    // remove item
    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<?> removeItem( @PathVariable BigInteger cartId, @PathVariable BigInteger itemId) {
        cartService.removeItem(cartId, itemId);
        return ResponseEntity.ok("Item removed successfully");
    }

    // delete cart
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable BigInteger cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.ok("Cart deleted successfully");
    }

    // complete purchase
    @PostMapping("/{cartId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable BigInteger cartId) {
        cartService.checkoutCart(cartId);
        return ResponseEntity.ok("Purchase completed successfully");
    }
}