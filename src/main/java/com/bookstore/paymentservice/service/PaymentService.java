package com.bookstore.paymentservice.service;

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

    public Payment processPayment(Long orderId, double amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(amount);
        payment = paymentRepository.save(payment);

        // Simulate payment processing
        boolean isSuccess = new Random().nextBoolean();
        return confirmPayment(payment.getId(), isSuccess);
    }

    public Payment confirmPayment(Long paymentId, boolean isSuccess) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(isSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentStatus(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
