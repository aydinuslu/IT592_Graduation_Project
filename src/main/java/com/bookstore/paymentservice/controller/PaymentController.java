package com.bookstore.paymentservice.controller;

import com.bookstore.paymentservice.model.Payment;
import com.bookstore.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Payment getPaymentStatus(@PathVariable Long orderId) {
        return paymentService.getPaymentStatus(orderId);
    }

    @PostMapping("/confirm")
    public Payment confirmPayment(@RequestParam Long paymentId, @RequestParam boolean isSuccess) {
        return paymentService.confirmPayment(paymentId, isSuccess);
    }
}
