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
            throw new RuntimeException("Book not found");
        }
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
        kafkaProducer.sendMessage("Deleted book with id: " + id);
    }
}
