package com.bookstore.paymentservice.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final String BOOK_TOPIC = "book-topic";
    private static final String CART_TOPIC = "cart-topic";
    private static final String ORDER_TOPIC = "order-topic";
    private static final String USER_TOPIC = "user-topic";
    private static final String PAYMENT_TOPIC = "payment-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendBookMessage(String message) {
        kafkaTemplate.send(BOOK_TOPIC, message);
    }

    public void sendCartMessage(String message) {
        kafkaTemplate.send(CART_TOPIC, message);
    }

    public void sendOrderMessage(String message) {
        kafkaTemplate.send(ORDER_TOPIC, message);
    }

    public void sendUserMessage(String message) {
        kafkaTemplate.send(USER_TOPIC, message);
    }

    public void sendPaymentMessage(String message) {
        kafkaTemplate.send(PAYMENT_TOPIC, message);
    }
}
