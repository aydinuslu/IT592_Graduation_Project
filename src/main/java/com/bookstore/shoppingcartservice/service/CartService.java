package com.bookstore.shoppingcartservice.service;

import com.bookstore.shoppingcartservice.kafka.KafkaProducer;
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

    @Autowired
    private KafkaProducer kafkaProducer;

    public Cart addItemToCart(Long userId, Long bookId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> createCart(userId));
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setBookId(bookId);
        cartItem.setQuantity(quantity);
        cart.getItems().add(cartItem);

        Cart savedCart = cartRepository.save(cart);

        // Produce Kafka message after adding item to cart
        String message = "Item added to cart: UserId=" + userId + ", BookId=" + bookId + ", Quantity=" + quantity;
        kafkaProducer.sendCartMessage(message);

        return savedCart;
    }

    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public void removeItemFromCart(Long userId, Long bookId) {
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.getItems().removeIf(item -> item.getBookId().equals(bookId));
            cartRepository.save(cart);

            // Produce Kafka message after removing item from cart
            String message = "Item removed from cart: UserId=" + userId + ", BookId=" + bookId;
            kafkaProducer.sendCartMessage(message);
        }
    }

    private Cart createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }
}
