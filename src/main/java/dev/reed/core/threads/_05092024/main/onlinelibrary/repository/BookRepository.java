package dev.reed.core.threads._05092024.main.onlinelibrary.repository;

import dev.reed.core.threads._05092024.main.onlinelibrary.entity.Book;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class BookRepository {

    private final ConcurrentMap<Long, Book> bookIdToBookMap = new ConcurrentHashMap<>();

    public void create(final Book book) {
        long bookId = generateId();
        book.setId(bookId);
        bookIdToBookMap.put(bookId, book);
    }

    public Optional<Book> getById(final Long bookId) {
        return Optional.ofNullable(bookIdToBookMap.get(bookId));
    }

    public Book update(final Book book) {
        return Optional.ofNullable(bookIdToBookMap.get(book.getId()))
                .map(current -> updateBookFields(book))
                .orElseThrow();
    }

    public boolean deleteById(final Long bookId) {
        return this.bookIdToBookMap.remove(bookId) != null;
    }

    public Collection<Book> getAll() {
        return this.bookIdToBookMap.values();
    }

    public Book save(final Book book) {
        return bookIdToBookMap.put(book.getId(), book);
    }

    private static Book updateBookFields(final Book book) {
        Function<Book, Book> updateBookFieldsFunction = current -> {
            current.setTitle(book.getTitle());
            current.setAuthor(book.getAuthor());
            current.setAvailable(book.isAvailable());
            return current;
        };
        return updateBookFieldsFunction.apply(book);
    }

    private static long generateId() {
        return ThreadLocalRandom.current().nextLong(1, 100_000_000);
    }
}
