package dev.reed.core.threads.starvation;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Your task is to implement a starvation through the unfair locking.
 * Please use {@link ReentrantLock} for that.
 * Hint: a resource can be accessed by the same thread multiple times (unfair locking), so the others are
 * keep trying to acquire a lock, but have no luck on that most of the time, which leads to a starvation.
 */
public class UnfairReentrantLockStarvation {

    private final Lock lock = new ReentrantLock();

    public void doWork() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " is making some noise");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        UnfairReentrantLockStarvation starvation = new UnfairReentrantLockStarvation();

        Runnable task = () -> {
            while (true) {
                starvation.doWork();
            }
        };

        var first = new Thread(task, "Thread-1");
        var second = new Thread(task, "Thread-2");
        var third = new Thread(task, "Thread-3");

        first.start();
        second.start();
        third.start();
    }
}
