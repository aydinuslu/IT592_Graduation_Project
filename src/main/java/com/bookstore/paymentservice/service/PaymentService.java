package com.bookstore.paymentservice.service;

import com.bookstore.paymentservice.kafka.KafkaProducer;
import com.bookstore.paymentservice.model.Payment;
import com.bookstore.paymentservice.model.PaymentStatus;
import com.bookstore.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    public Payment processPayment(Long orderId, double amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(amount);
        payment = paymentRepository.save(payment);

        // Send payment initiation message
        kafkaProducer.sendPaymentMessage(String.format("Payment initiated: PaymentID=%d, OrderID=%d, Amount=%.2f",
                payment.getId(), payment.getOrderId(), payment.getAmount()));

        // Simulate payment processing
        boolean isSuccess = new Random().nextBoolean();
        //boolean isSuccess = true; // or set to false to simulate failure
        return confirmPayment(payment.getId(), isSuccess);
    }

    public Payment confirmPayment(Long paymentId, boolean isSuccess) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(isSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        Payment updatedPayment = paymentRepository.save(payment);

        // Send payment confirmation message
        kafkaProducer.sendPaymentMessage(String.format("Payment %s: PaymentID=%d, OrderID=%d, Amount=%.2f",
                isSuccess ? "completed" : "failed", updatedPayment.getId(), updatedPayment.getOrderId(), updatedPayment.getAmount()));

        return updatedPayment;
    }

    public Payment getPaymentStatus(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
