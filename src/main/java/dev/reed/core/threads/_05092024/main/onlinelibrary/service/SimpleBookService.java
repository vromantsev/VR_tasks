package dev.reed.core.threads._05092024.main.onlinelibrary.service;

import dev.reed.core.threads._05092024.main.onlinelibrary.entity.Book;
import dev.reed.core.threads._05092024.main.onlinelibrary.repository.BookRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SimpleBookService implements BookService {

    private final BookRepository bookRepository;

    @Override
    public List<Book> getByAuthor(final String author) {
        return List.of();
    }

    @Override
    public Book getById(Long bookId) {
        return null;
    }

    @Override
    public Book rent(String title, String rentedBy) {
        return null;
    }

    @Override
    public void returnBook(Book book) {

    }
}
