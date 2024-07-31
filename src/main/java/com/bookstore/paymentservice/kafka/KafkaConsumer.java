package com.bookstore.paymentservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "cart-topic", groupId = "shopping-cart-service-group")
    public void consumeCartMessage(String message) {
        System.out.println("Consumed cart message: " + message);
    }

    @KafkaListener(topics = "payment-topic", groupId = "payment-service-group")
    public void consumePaymentMessage(String message) {
        System.out.println("Consumed payment message: " + message);
    }
}
