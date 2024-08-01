package com.bookstore.bookcatalogservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "book-topic", groupId = "book-catalog-service-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}
