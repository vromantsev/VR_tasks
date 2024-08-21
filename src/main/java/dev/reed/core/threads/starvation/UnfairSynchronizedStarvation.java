package dev.reed.core.threads.starvation;

import java.util.concurrent.TimeUnit;

/**
 * Your task is to implement a starvation using "synchronized" keyword.
 * The critical section cannot be accessed by multiple threads, because a lock is always acquired by a
 * single thread, while others are keep trying to enter the critical section.
 */
public class UnfairSynchronizedStarvation {

    public synchronized void doWork() {
        System.out.println(Thread.currentThread().getName() + " acquired a lock");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        UnfairSynchronizedStarvation starvation = new UnfairSynchronizedStarvation();

        Runnable task = () -> {
            while (true) {
                starvation.doWork();
            }
        };

        Thread first = new Thread(task, "Thread 1");
        Thread second = new Thread(task, "Thread 2");
        Thread third = new Thread(task, "Thread 3");

        first.start();
        second.start();
        third.start();
    }
}
