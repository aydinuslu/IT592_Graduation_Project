package com.bookstore.shoppingcartservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "cart-topic", groupId = "shopping-cart-service-group")
    public void consumeCartMessage(String message) {
        System.out.println("Consumed cart message: " + message);
        // Process the cart message
    }

    @KafkaListener(topics = "order-topic", groupId = "order-service-group")
    public void consumeOrderMessage(String message) {
        System.out.println("Consumed order message: " + message);
        // Process the order message, e.g., clear the cart
    }
}
