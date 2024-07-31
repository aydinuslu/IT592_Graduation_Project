package com.bookstore.shoppingcartservice.service;

import com.bookstore.shoppingcartservice.model.Cart;
import com.bookstore.shoppingcartservice.model.CartItem;
import com.bookstore.shoppingcartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Cart addItemToCart(Long userId, Long bookId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> createCart(userId));
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setBookId(bookId);
        cartItem.setQuantity(quantity);
        cart.getItems().add(cartItem);
        return cartRepository.save(cart);
    }

    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    private Cart createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }
}
