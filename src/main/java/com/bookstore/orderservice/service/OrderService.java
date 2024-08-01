package com.bookstore.orderservice.service;

import com.bookstore.orderservice.kafka.KafkaProducer;
import com.bookstore.orderservice.model.Order;
import com.bookstore.orderservice.model.OrderItem;
import com.bookstore.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

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

        Order savedOrder = orderRepository.save(order);

        // Create detailed order message
        String orderDetails = String.format("Order placed: ID=%d, UserID=%d, OrderDate=%s, Status=%s, Items=[%s]",
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getOrderDate(),
                savedOrder.getStatus(),
                savedOrder.getItems().stream()
                        .map(item -> String.format("ItemID=%d, BookID=%d, Quantity=%d, Price=%.2f",
                                item.getId(), item.getBookId(), item.getQuantity(), item.getPrice()))
                        .collect(Collectors.joining(", ")));

        kafkaProducer.sendOrderMessage(orderDetails);
        return savedOrder;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }
}
