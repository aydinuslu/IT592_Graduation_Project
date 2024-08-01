package com.bookstore.shoppingcartservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final String CART_TOPIC = "cart-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendCartMessage(String message) {
        kafkaTemplate.send(CART_TOPIC, message);
    }
}
