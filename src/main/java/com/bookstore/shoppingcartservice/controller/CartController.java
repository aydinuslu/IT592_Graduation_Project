package com.bookstore.shoppingcartservice.controller;

import com.bookstore.shoppingcartservice.model.Cart;
import com.bookstore.shoppingcartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
