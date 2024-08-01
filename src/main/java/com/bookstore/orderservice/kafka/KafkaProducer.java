package com.bookstore.orderservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final String ORDER_TOPIC = "order-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderMessage(String message) {
        kafkaTemplate.send(ORDER_TOPIC, message);
    }
}
