package com.bookstore.orderservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "order-topic", groupId = "order-service-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}
