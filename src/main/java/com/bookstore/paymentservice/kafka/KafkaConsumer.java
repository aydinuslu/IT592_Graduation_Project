package com.bookstore.paymentservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "payment-topic", groupId = "payment-service-group")
    public void consumePaymentMessage(String message) {
        System.out.println("Consumed payment message: " + message);
    }
}
