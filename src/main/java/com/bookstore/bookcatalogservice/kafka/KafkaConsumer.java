package com.bookstore.bookcatalogservice.kafka;

import com.bookstore.bookcatalogservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private BookService bookService;

    // To keep track of successfully completed orders that need stock updates
    private Set<Long> completedOrders = new HashSet<>();

    @KafkaListener(topics = "payment-topic", groupId = "payment-service-group")
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
            } catch (Exception e) {
                logger.error("Failed to process payment message: {}", message, e);
            }
        } else {
            logger.debug("Payment message did not indicate completion, ignoring: {}", message);
        }
    }

    @KafkaListener(topics = "order-topic", groupId = "order-service-group")
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

                    // Parse book IDs and quantities
                    Pattern pattern = Pattern.compile("BookID=(\\d+), Quantity=(\\d+)");
                    Matcher matcher = pattern.matcher(message);
                    while (matcher.find()) {
                        Long bookId = Long.parseLong(matcher.group(1));
                        int quantity = Integer.parseInt(matcher.group(2));
                        logger.debug("Updating stock for BookID={} with Quantity={}", bookId, quantity);

                        // Update stock for each book in the order
                        bookService.updateStock(bookId, quantity);
                    }

                    // Once processed, remove from the set
                    completedOrders.remove(orderId);
                    logger.info("Successfully processed and updated stock for OrderID={}. Removed from completedOrders set.", orderId);
                } else {
                    logger.warn("OrderID={} was not found in completedOrders set. Stock update skipped.", orderId);
                }
            } catch (Exception e) {
                logger.error("Failed to process order message: {}", message, e);
            }
        } else {
            logger.debug("Order message did not indicate placement, ignoring: {}", message);
        }
    }
}
