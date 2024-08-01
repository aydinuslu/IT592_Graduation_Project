package com.bookstore.paymentservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final String PAYMENT_TOPIC = "payment-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendPaymentMessage(String message) {
        kafkaTemplate.send(PAYMENT_TOPIC, message);
    }
}
