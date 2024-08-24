package com.bookstore.shoppingcartservice.controller;

import com.bookstore.shoppingcartservice.exception.InsufficientStockException;
import com.bookstore.shoppingcartservice.model.Cart;
import com.bookstore.shoppingcartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/{userId}/add")
    public Cart addItemToCart(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        Long bookId = Long.valueOf(request.get("bookId").toString());
        int quantity = Integer.parseInt(request.get("quantity").toString());
        return cartService.addItemToCart(userId, bookId, quantity);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // removeItemFromCart
    @DeleteMapping("/{userId}/remove/{bookId}")
    public void removeItemFromCart(@PathVariable Long userId, @PathVariable Long bookId) {
        cartService.removeItemFromCart(userId, bookId);
    }

    @PutMapping("/{userId}/update/{bookId}")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long bookId,
            @RequestBody Map<String, Object> request) {
        try {
            int quantity = Integer.parseInt(request.get("quantity").toString());
            return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, bookId, quantity));
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}
