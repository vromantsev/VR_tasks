package dev.reed.core.threads._05092024.main.onlinelibrary.service;

import dev.reed.core.threads._05092024.main.onlinelibrary.entity.Book;

import java.util.List;

public interface BookService {

    List<Book> getByAuthor(String author);

    Book getById(Long bookId);

    Book rent(String title, String rentedBy);

    void returnBook(Book book);
}
