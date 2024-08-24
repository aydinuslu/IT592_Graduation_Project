package com.bookstore.bookcatalogservice.service;

import com.bookstore.bookcatalogservice.kafka.KafkaProducer;
import com.bookstore.bookcatalogservice.model.Book;
import com.bookstore.bookcatalogservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

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

    // Update stock of a book
    public void updateStock(Long bookId, int quantity) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();

            // Check if there's enough stock to fulfill the request
            if (book.getStock() < quantity) {
                throw new RuntimeException("Not enough stock for book ID: " + bookId);
            }

            // Decrement the stock by the given quantity
            book.setStock(book.getStock() - quantity);

            // Save the updated book to the repository
            Book updatedBook = bookRepository.save(book);

            // Log a message that the stock was updated
            kafkaProducer.sendMessage("Updated stock for book ID: " + updatedBook.getId() + " to new stock: " + updatedBook.getStock());
        } else {
            throw new RuntimeException("Book not found for ID: " + bookId);
        }
    }
}
