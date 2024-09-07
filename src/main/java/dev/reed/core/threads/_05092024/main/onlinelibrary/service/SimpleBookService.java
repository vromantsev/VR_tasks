package dev.reed.core.threads._05092024.main.onlinelibrary.service;

import dev.reed.core.threads._05092024.main.onlinelibrary.entity.Book;
import dev.reed.core.threads._05092024.main.onlinelibrary.lock.ExtendedReadWriteLock;
import dev.reed.core.threads._05092024.main.onlinelibrary.repository.BookRepository;
import lombok.Getter;

import java.util.List;

public class SimpleBookService implements BookService {

    private final BookRepository bookRepository;
    @Getter
    private final ExtendedReadWriteLock lock;

    public SimpleBookService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        // TODO: pass ReentrantReadWriteLock as an argument to ExtendedReadWriteLock
        this.lock = new ExtendedReadWriteLock(null);
    }

    /**
     * Acquired write lock and call lock() method
     * Create a book via {@link BookRepository#create(Book)}
     * Release a lock
     */
    @Override
    public void create(final Book book) {

    }

    /**
     * Acquire read lock and call lock() method
     * Find books by author using {@link BookRepository#getAll()}
     * Release a lock
     */
    @Override
    public List<Book> getByAuthor(final String author) {
        return List.of();
    }

    /**
     * Acquire read lock and call lock() method
     * Find a book by id using {@link BookRepository#getById(Long)}
     * Release a lock
     */
    @Override
    public Book getById(final Long bookId) {
        return null;
    }

    /**
     * Acquire write lock
     * Use {@link BookRepository#getAll()}
     * Filter the book by title and make sure the book is available
     * Rent a book - set rentedBy and rentedAt fields + change field available to false
     * Release a lock
     */
    @Override
    public Book rent(final String title, final String rentedBy) {
        return null;
    }

    /**
     * Acquire write lock
     * Use {@link BookRepository#getById(Long)} and implement returning logic - set rentedBy and rentedAt fields to null,
     * and field available to true
     * Release a lock
     */
    @Override
    public void returnBook(final Book book) {

    }

    /**
     * Acquire write lock
     * Use {@link BookRepository#deleteById(Long)} method
     * Release a lock
     */
    @Override
    public boolean deleteById(final Long bookId) {
        return false;
    }
}
