package dev.reed.core.threads._05092024.main.onlinelibrary.service;

import dev.reed.core.threads._05092024.main.onlinelibrary.entity.Book;
import dev.reed.core.threads._05092024.main.onlinelibrary.repository.BookRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor
public class SimpleBookService implements BookService {

    private final BookRepository bookRepository;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

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

    @Override
    public Book rent(final String title, final String rentedBy) {
        Objects.requireNonNull(title, "Parameter [title] must not be empty!");
        Objects.requireNonNull(rentedBy, "Parameter [rentedBy] must not be empty!");
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
