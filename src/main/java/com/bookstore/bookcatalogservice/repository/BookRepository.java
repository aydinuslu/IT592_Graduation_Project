package com.bookstore.bookcatalogservice.repository;

import com.bookstore.bookcatalogservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
