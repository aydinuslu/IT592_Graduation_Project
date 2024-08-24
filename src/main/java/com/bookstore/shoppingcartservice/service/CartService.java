package com.bookstore.shoppingcartservice.service;

import com.bookstore.shoppingcartservice.exception.InsufficientStockException;
import com.bookstore.shoppingcartservice.kafka.KafkaProducer;
import com.bookstore.shoppingcartservice.model.Cart;
import com.bookstore.shoppingcartservice.model.CartItem;
import com.bookstore.shoppingcartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${book.catalog.service.url}")
    private String bookServiceUrl;

    public Cart addItemToCart(Long userId, Long bookId, int quantity) {
        int availableStock = getAvailableStock(bookId);
        if (quantity > availableStock) {
            throw new RuntimeException("Requested quantity exceeds available stock");
        }

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

    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long bookId, int quantity) {
        try {
            int availableStock = getAvailableStock(bookId);
            if (quantity > availableStock) {
                throw new InsufficientStockException("Requested quantity exceeds available stock");
            }

            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            CartItem item = cart.getItems().stream()
                    .filter(cartItem -> cartItem.getBookId().equals(bookId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Book not found in cart"));

            item.setQuantity(quantity);

            // Save the cart with updated item
            Cart updatedCart = cartRepository.save(cart);

            // Produce Kafka message after updating item in cart
            String message = "Item updated in cart: UserId=" + userId + ", BookId=" + bookId + ", Quantity=" + quantity;
            kafkaProducer.sendCartMessage(message);

            return updatedCart;
        } catch (InsufficientStockException e) {
            // Log the exception and rethrow it so it can be handled in the controller
            throw new InsufficientStockException(e.getMessage());
        }
    }

    public void clearCart(Long userId) {
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.getItems().clear();
            cartRepository.save(cart);

            // Produce Kafka message after clearing the cart
            String message = "Cart cleared: UserId=" + userId;
            kafkaProducer.sendCartMessage(message);
        } else {
            throw new RuntimeException("Cart not found for user: " + userId);
        }
    }

    private int getAvailableStock(Long bookId) {
        String url = bookServiceUrl + "/stock/" + bookId;
        Integer stock = restTemplate.getForObject(url, Integer.class);
        if (stock == null) {
            throw new RuntimeException("Failed to retrieve stock for bookId: " + bookId);
        }
        return stock;
    }
}
