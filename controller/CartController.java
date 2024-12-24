package com.spring.controller;

import com.spring.entities.Cart;
import com.spring.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/e-com/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{cartId}/my-cart")
    public ResponseEntity<Cart> getCart( @PathVariable Long cartId) {
        try {
            return ResponseEntity.ok(cartService.getCart(cartId));
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }

    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<String> clearCart( @PathVariable Long cartId) {
        try {
            cartService.clearCart(cartId);
            return ResponseEntity.ok("Successfully cart cleared");
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }

    @GetMapping("/{cartId}/cart/total-price")
    public ResponseEntity<BigDecimal> getTotalAmount(@PathVariable Long cartId) {
        try {
            return ResponseEntity.ok(cartService.getTotalPrice(cartId));
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }
}
