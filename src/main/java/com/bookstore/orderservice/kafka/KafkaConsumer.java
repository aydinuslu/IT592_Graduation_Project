package com.bookstore.orderservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "order-topic", groupId = "order-service-group")
    public void consumeOrderTopic(String message) {
        System.out.println("Consumed message from order-topic: " + message);
    }

    @KafkaListener(topics = "cart-topic", groupId = "shopping-cart-service-group")
    public void consumeCartTopic(String message) {
        System.out.println("Consumed message from cart-topic: " + message);
    }
}
