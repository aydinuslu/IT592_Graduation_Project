package com.bookstore.paymentservice.controller;

import com.bookstore.paymentservice.model.Payment;
import com.bookstore.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public Payment processPayment(@RequestParam Long orderId, @RequestParam double amount) {
        return paymentService.processPayment(orderId, amount);
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentStatus(orderId);
        if (payment == null) {
            // Return a 204 No Content if no payment is found for the order
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/confirm")
    public Payment confirmPayment(@RequestParam Long paymentId, @RequestParam boolean isSuccess) {
        return paymentService.confirmPayment(paymentId, isSuccess);
    }
}
