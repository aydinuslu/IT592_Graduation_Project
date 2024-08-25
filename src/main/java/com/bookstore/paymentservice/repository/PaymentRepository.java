package com.bookstore.paymentservice.repository;

import com.bookstore.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Find the most recent payment by orderId
    Optional<Payment> findFirstByOrderIdOrderByPaymentDateDesc(Long orderId);
}
