package dev.reed.core.threads._05092024.main.onlinelibrary.service;

import dev.reed.core.threads._05092024.main.onlinelibrary.entity.Book;
import dev.reed.core.threads._05092024.main.onlinelibrary.lock.ExtendedReadWriteLock;
import dev.reed.core.threads._05092024.main.onlinelibrary.repository.BookRepository;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleBookService implements BookService {

    private final BookRepository bookRepository;
    @Getter
    private final ExtendedReadWriteLock lock;

    public SimpleBookService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.lock = new ExtendedReadWriteLock(new ReentrantReadWriteLock());
    }

    /**
     * Acquired write lock and call lock() method
     * Create a book via {@link BookRepository#create(Book)}
     * Release a lock
     */
    @Override
    public void create(final Book book) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            this.bookRepository.create(book);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Acquire read lock and call lock() method
     * Find books by author using {@link BookRepository#getAll()}
     * Release a lock
     */
    @Override
    public List<Book> getByAuthor(final String author) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return bookRepository.getAll()
                    .stream()
                    .filter(b -> b.getAuthor().equals(author))
                    .toList();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot find books by author %s".formatted(author), ex);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Acquire read lock and call lock() method
     * Find a book by id using {@link BookRepository#getById(Long)}
     * Release a lock
     */
    @Override
    public Book getById(final Long bookId) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return bookRepository.getById(bookId)
                    .orElseThrow(() -> new RuntimeException("Cannot find a book by id=%d".formatted(bookId)));
        } catch (Exception ex) {
            throw new RuntimeException("Cannot find a book by id=%d, unexpected error occurred".formatted(bookId), ex);
        } finally {
            readLock.unlock();
        }
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
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            return this.bookRepository.getAll()
                    .stream()
                    .filter(book -> book.getTitle().equals(title))
                    .filter(Book::isAvailable)
                    .findAny()
                    .map(b -> rentBook(b, rentedBy))
                    .orElseThrow(() -> new RuntimeException("Cannot rent a book with title '%s'".formatted(title)));
        } catch (Exception ex) {
            throw new RuntimeException("Cannot rent a book '%s', unexpected error occurred".formatted(title), ex);
        } finally {
            writeLock.unlock();
        }
    }

    private Book rentBook(final Book book, final String rentedBy) {
        book.setRentedBy(rentedBy);
        book.setRentedAt(LocalDateTime.now());
        book.setAvailable(false);
        this.bookRepository.save(book);
        return book;
    }

    /**
     * Acquire write lock
     * Use {@link BookRepository#getById(Long)} and implement returning logic - set rentedBy and rentedAt fields to null,
     * and field available to true
     * Release a lock
     */
    @Override
    public void returnBook(final Book book) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            this.bookRepository.getById(book.getId())
                    .map(this::doReturnBook)
                    .orElseThrow(() -> new RuntimeException("Cannot return a book '%s'".formatted(book.getTitle())));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to return a book '%s', unexpected error occurred".formatted(book.getTitle()));
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Acquire write lock
     * Use {@link BookRepository#deleteById(Long)} method
     * Release a lock
     */
    @Override
    public boolean deleteById(final Long bookId) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            return this.bookRepository.deleteById(bookId);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot delete book by id=%d".formatted(bookId), ex);
        } finally {
            writeLock.unlock();
        }
    }

    private Book doReturnBook(Book book) {
        book.setRentedBy(null);
        book.setRentedAt(null);
        book.setAvailable(true);
        this.bookRepository.save(book);
        return book;
    }
}
