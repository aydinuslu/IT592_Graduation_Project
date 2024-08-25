package com.bookstore.bookcatalogservice.service;

import com.bookstore.bookcatalogservice.kafka.KafkaProducer;
import com.bookstore.bookcatalogservice.model.Book;
import com.bookstore.bookcatalogservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book createBook(Book book) {
        Book savedBook = bookRepository.save(book);
        kafkaProducer.sendMessage("Created book with id: " + savedBook.getId());
        return savedBook;
    }

    public Book updateBook(Long id, Book bookDetails) {
        Optional<Book> optionalBook = bookRepository.findById(id);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setPrice(bookDetails.getPrice());
            book.setStock(bookDetails.getStock());

            Book updatedBook = bookRepository.save(book);
            kafkaProducer.sendMessage("Updated book with id: " + updatedBook.getId());
            return updatedBook;
        } else {
            return null;
        }
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
        kafkaProducer.sendMessage("Deleted book with id: " + id);
    }

    // Get stock of a book
    public int getStock(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(Book::getStock).orElse(-1);
    }

    public void updateStock(Long bookId, int quantity) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();

            if (book.getStock() >= quantity) {
                book.setStock(book.getStock() - quantity);
                bookRepository.save(book);
                logger.info("Stock updated for BookID={}, new stock={}", bookId, book.getStock());
            } else {
                logger.error("Not enough stock for book ID: {}. Available: {}, Requested: {}", bookId, book.getStock(), quantity);
                // Optionally, you could raise an alert or flag this condition for further review.
            }
        } else {
            logger.error("Book ID: {} not found in the repository.", bookId);
        }
    }
}