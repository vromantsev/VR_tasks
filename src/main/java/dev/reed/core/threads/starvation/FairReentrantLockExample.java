package dev.reed.core.threads.starvation;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Your task is to implement a version of a "fair" lock to avoid starvation problem.
 * Please use {@link ReentrantLock} for this implementation.
 * You can also come up with your own solution to this problem.
 */
public class FairReentrantLockExample {

    private final Lock lock = new ReentrantLock(true);

    public void doWork() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " is making some noise");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        FairReentrantLockExample example = new FairReentrantLockExample();

        Runnable task = () -> {
            while (true) {
                example.doWork();
            }
        };

        Thread first = new Thread(task, "Thread-1");
        Thread second = new Thread(task, "Thread-2");
        Thread third = new Thread(task, "Thread-3");

        first.start();
        second.start();
        third.start();
    }
}
