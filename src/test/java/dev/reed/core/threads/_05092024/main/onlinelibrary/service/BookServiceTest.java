package dev.reed.core.threads._05092024.main.onlinelibrary.service;


import dev.reed.core.threads._05092024.main.onlinelibrary.entity.Book;
import dev.reed.core.threads._05092024.main.onlinelibrary.repository.BookRepository;
import dev.reed.core.threads._05092024.main.onlinelibrary.utils.CommonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookServiceTest {

    private final BookRepository bookRepository = new BookRepository();
    private final BookService bookService = new SimpleBookService(bookRepository);
    private final Field readWriteLockField = CommonUtils.getReadWriteLockField(bookService.getClass());

    @BeforeEach
    public void setup() {
        createBooks();
    }

    @AfterEach
    public void tearDown() {
        bookRepository.clear();
    }

    @Test
    @Order(1)
    public void shouldCreateNewBook() {
        bookRepository.clear();

        this.bookService.create(
                Book.builder()
                        .title("Concurrent programming")
                        .author("Unknown")
                        .available(true)
                        .build()
        );

        assertNotNull(readWriteLockField, "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isWriteLockUsed"), "Write lock must be acquired for this operation!");
        assertEquals(1, bookRepository.getAll().size(), "Invalid number of books, expected 1");
    }

    @Test
    @Order(2)
    public void shouldGetBooksByAuthor() {
        List<Book> books = this.bookService.getByAuthor("Vlad Mihalcea");

        assertNotNull(readWriteLockField, "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isReadLockUsed"), "Read lock must be acquired for this operation!");
        assertEquals(1, books.size(), "Invalid number of books for a given author, expected 1");
    }

    @Test
    @Order(3)
    public void shouldGetNoBooksByInvalidAuthor() {
        List<Book> books = this.bookService.getByAuthor("Fake author");

        assertNotNull(readWriteLockField, "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isReadLockUsed"), "Read lock must be acquired for this operation!");
        assertTrue(books.isEmpty(), "No books is expected for the invalid author");
    }

    @Test
    @Order(4)
    @Timeout(value = 5)
    public void shouldGetRelevantBooksViaConcurrentAccess() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isReadLockUsed"), "Read lock must be acquired for this operation!");
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    List<Book> books = bookService.getByAuthor("Refactoring Guru");
                    assertEquals(1, books.size());
                    assertEquals("Refactoring Guru", books.get(0).getAuthor());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }

    @Test
    @Order(5)
    public void shouldGetBookById() {
        Book first = this.bookRepository.getAll().iterator().next();
        Book book = this.bookService.getById(first.getId());
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isReadLockUsed"), "Read lock must be acquired for this operation!");
        assertEquals(first, book, "Invalid book, expected %s".formatted(first));
    }

    @Test
    @Order(6)
    @Timeout(value = 5)
    public void shouldGetBookByIdConcurrently() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        Book first = this.bookRepository.getAll().iterator().next();
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isReadLockUsed"), "Read lock must be acquired for this operation!");
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    Book foundBook = bookService.getById(first.getId());
                    assertEquals(first.getTitle(), foundBook.getTitle());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }

    @Test
    @Order(7)
    public void shouldRentBookSuccessfully() {
        Book rentedBook = bookService.rent("Design patterns", "Petro Petrovych");
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isWriteLockUsed"), "Write lock must be acquired for this operation!");
        assertNotNull(rentedBook, "Book should be rented successfully");
        assertEquals("Petro Petrovych", rentedBook.getRentedBy(), "RentedBy should be set to the user");
        assertFalse(rentedBook.isAvailable(), "Book should be marked as unavailable");
    }

    @Test
    @Order(8)
    public void shouldThrowExceptionForNonExistentBookToRent() {
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertThrows(RuntimeException.class, () -> bookService.rent("Nonexistent Book", "User"),
                "Should throw an exception if the book does not exist");
    }

    @Test
    @Order(9)
    public void shouldFailToRentAlreadyRentedBook() {
        Book book = bookService.getByAuthor("Vlad Mihalcea").iterator().next();
        book.setAvailable(false);
        book.setRentedBy("Some user");
        book.setRentedAt(LocalDateTime.now());
        bookRepository.save(book);
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertThrows(RuntimeException.class, () -> bookService.rent("Vlad Mihalcea", "John Snow"),
                "Should throw an exception if the book is already rented");
    }

    @Test
    @Order(10)
    @Timeout(value = 5)
    public void shouldTestConcurrentRentAttempts() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<Book> rentedBookOneRef = new AtomicReference<>();
        AtomicReference<Book> rentedBookTwoRef = new AtomicReference<>();

        executorService.submit(() -> {
            try {
                rentedBookOneRef.set(bookService.rent("High Performance Java Persistence", "User1"));
            } catch (Exception ignored) {
            }
            latch.countDown();
        });

        executorService.submit(() -> {
            try {
                rentedBookTwoRef.set(bookService.rent("High Performance Java Persistence", "User2"));
            } catch (Exception ignored) {
            }
            latch.countDown();
        });

        latch.await();
        executorService.shutdown();
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isWriteLockUsed"), "Write lock must be acquired for this operation!");
        assertNotNull(rentedBookOneRef.get(), "User %s should successfully rent the book".formatted("User1"));
        assertNull(rentedBookTwoRef.get(), "User %s should not be able to rent the book".formatted("User2"));
    }

    @Test
    @Order(11)
    public void shouldReturnBookSuccessfully() {
        Book book = bookRepository.getAll().iterator().next();
        book.setAvailable(false);
        book.setRentedBy("Test user");
        book.setRentedAt(LocalDateTime.now());

        bookService.returnBook(book);

        Book returnedBook = bookRepository.getById(book.getId()).orElseThrow();
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isWriteLockUsed"), "Write lock must be acquired for this operation!");
        assertTrue(returnedBook.isAvailable(), "Book should be marked as available");
        assertNull(returnedBook.getRentedBy(), "RentedBy should be null after returning the book");
        assertNull(returnedBook.getRentedAt(), "RentedAt should be null after returning the book");
    }

    @Test
    @Order(12)
    @Timeout(value = 5)
    public void shouldTestConcurrentReturnAttempt() throws InterruptedException {
        Book book = bookRepository.getAll().iterator().next();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isWriteLockUsed"), "Write lock must be acquired for this operation!");
        executorService.submit(() -> {
            try {
                bookService.returnBook(book);
            } catch (Exception ignored) {
            }
            latch.countDown();
        });

        executorService.submit(() -> {
            try {
                bookService.returnBook(book);
            } catch (Exception ignored) {
            }
            latch.countDown();
        });

        latch.await();
        executorService.shutdown();

        Book returnedBook = bookRepository.getById(book.getId()).orElseThrow();
        assertTrue(returnedBook.isAvailable(), "Book should be available after return");
        assertNull(returnedBook.getRentedBy(), "RentedBy should be null after returning the book");
        assertNull(returnedBook.getRentedAt(), "RentedAt should be null after returning the book");
    }

    @Test
    @Order(13)
    public void shouldDeleteBookByIdSuccessfully() {
        Book book = bookRepository.getAll().iterator().next();
        boolean deleted = bookService.deleteById(book.getId());
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isWriteLockUsed"), "Write lock must be acquired for this operation!");
        assertTrue(deleted, "Book %s should be deleted!".formatted(book.getTitle()));
    }

    @Test
    @Order(14)
    @Timeout(value = 5)
    public void shouldTestConcurrentRemoveAttempt() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        Book book = bookRepository.getAll().iterator().next();
        executorService.submit(() -> {
            try {
                bookService.deleteById(book.getId());
            } catch (Exception ignored) {
            }
            latch.countDown();
        });

        executorService.submit(() -> {
            try {
                bookService.deleteById(book.getId());
            } catch (Exception ignored) {
            }
            latch.countDown();
        });

        latch.await();
        executorService.shutdown();
        assertNotNull(CommonUtils.getReadWriteLockField(bookService.getClass()), "It is mandatory for BookService to use lock, therefore it cannot be null!");
        assertEquals(ReadWriteLock.class.getName(), CommonUtils.getLockClassName(bookService.getClass()), "BookService implementation must use ReadWriteLock!");
        assertTrue(isLockUsed(readWriteLockField, "isWriteLockUsed"), "Write lock must be acquired for this operation!");
        assertFalse(bookRepository.getById(book.getId()).isPresent(), "Book should be removed from repository after concurrent delete attempts");
    }

    private void createBooks() {
        bookRepository.create(
                Book.builder()
                        .id(1L)
                        .author("Vlad Mihalcea")
                        .title("High Performance Java Persistence")
                        .available(true)
                        .build()
        );
        bookRepository.create(
                Book.builder()
                        .id(2L)
                        .author("Refactoring Guru")
                        .title("Design patterns")
                        .available(true)
                        .build()
        );
    }

    private boolean isLockUsed(final Field lockField, final String methodName) {
        Method isWriteLockUsed = CommonUtils.findMethod(lockField, methodName);
        try {
            return (boolean) isWriteLockUsed.invoke(((SimpleBookService) bookService).getLock());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
