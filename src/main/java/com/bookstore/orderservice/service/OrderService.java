package com.bookstore.orderservice.service;

import com.bookstore.orderservice.model.Order;
import com.bookstore.orderservice.model.OrderItem;
import com.bookstore.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Order placeOrder(Long userId, List<OrderItem> items) {
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PLACED");
        order.setItems(items);

        // Set the order reference in each OrderItem
        for (OrderItem item : items) {
            item.setOrder(order);
        }

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }
}
