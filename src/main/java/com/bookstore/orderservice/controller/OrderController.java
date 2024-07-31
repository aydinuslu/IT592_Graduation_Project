package com.bookstore.orderservice.controller;

import com.bookstore.orderservice.model.Order;
import com.bookstore.orderservice.model.OrderItem;
import com.bookstore.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/{userId}")
    public Order placeOrder(@PathVariable Long userId, @RequestBody List<OrderItem> items) {
        return orderService.placeOrder(userId, items);
    }

    @GetMapping("/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }
}
