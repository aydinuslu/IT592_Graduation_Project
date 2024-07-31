package com.bookstore.orderservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "cart-topic", groupId = "shopping-cart-service-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}
