package com.bookstore.bookcatalogservice.kafka;

import com.bookstore.bookcatalogservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private BookService bookService;

    // To keep track of successfully completed orders that need stock updates
    private Set<Long> completedOrders = new HashSet<>();
    // To store order messages that arrived before the payment completion
    private Map<Long, String> pendingOrderMessages = new HashMap<>();

    @KafkaListener(topics = "payment-topic", groupId = "book-catalog-service-group")
    public void consumePaymentMessage(String message) {
        logger.debug("Received payment message: {}", message);

        // Process the payment message
        if (message.contains("Payment completed")) {
            try {
                String orderIdStr = message.split("OrderID=")[1].split(",")[0];
                Long orderId = Long.parseLong(orderIdStr);

                // Add to the set of completed orders
                completedOrders.add(orderId);
                logger.info("Payment completed for OrderID={}. Added to completedOrders set.", orderId);

                // If there is a pending order message for this order, process it now
                if (pendingOrderMessages.containsKey(orderId)) {
                    processOrderMessage(pendingOrderMessages.remove(orderId));
                }
            } catch (Exception e) {
                logger.error("Failed to process payment message: {}", message, e);
            }
        } else {
            logger.debug("Payment message did not indicate completion, ignoring: {}", message);
        }
    }

    @KafkaListener(topics = "order-topic", groupId = "book-catalog-service-group")
    public void consumeOrderMessage(String message) {
        logger.debug("Received order message: {}", message);

        if (message.contains("Order placed")) {
            try {
                String orderIdStr = message.split("ID=")[1].split(",")[0];
                Long orderId = Long.parseLong(orderIdStr);
                logger.debug("Parsed OrderID: {}", orderId);

                // Only update stock if the order was marked as completed in the payment topic
                if (completedOrders.contains(orderId)) {
                    logger.info("OrderID={} found in completedOrders set. Proceeding to update stock.", orderId);
                    processOrderMessage(message);
                    completedOrders.remove(orderId); // Clean up after processing
                } else {
                    logger.warn("OrderID={} was not found in completedOrders set. Stock update deferred.", orderId);
                    pendingOrderMessages.put(orderId, message); // Store the order message until payment is confirmed
                }
            } catch (Exception e) {
                logger.error("Failed to process order message: {}", message, e);
            }
        } else {
            logger.debug("Order message did not indicate placement, ignoring: {}", message);
        }
    }

    private void processOrderMessage(String message) {
        try {
            Pattern pattern = Pattern.compile("BookID=(\\d+), Quantity=(\\d+)");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                Long bookId = Long.parseLong(matcher.group(1));
                int quantity = Integer.parseInt(matcher.group(2));
                logger.debug("Updating stock for BookID={} with Quantity={}", bookId, quantity);

                // Update stock for each book in the order
                bookService.updateStock(bookId, quantity);
            }
            logger.info("Successfully processed and updated stock for order.");
        } catch (Exception e) {
            logger.error("Failed to process the order message for stock update: {}", message, e);
        }
    }
}
