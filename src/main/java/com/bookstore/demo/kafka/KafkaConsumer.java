package com.bookstore.demo.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "user-topic", groupId = "user-service-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}
