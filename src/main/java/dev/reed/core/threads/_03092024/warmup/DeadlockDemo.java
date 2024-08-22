package dev.reed.core.threads._03092024.warmup;

import java.util.concurrent.TimeUnit;

/**
 * Your task is to implement a deadlock.
 */
public class DeadlockDemo {

    private static final Object LOCK_ONE = new Object();
    private static final Object LOCK_TWO = new Object();

    public static class Worker {

        public void doWork() {
            synchronized (LOCK_ONE) {
                System.out.println(Thread.currentThread().getName() + " acquired LOCK_ONE");
                synchronized (LOCK_TWO) {
                    System.out.println(Thread.currentThread().getName() + " acquired LOCK_TWO");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public void doAnotherWork() {
            synchronized (LOCK_TWO) {
                System.out.println(Thread.currentThread().getName() + " acquired LOCK_TWO");
                synchronized (LOCK_ONE) {
                    System.out.println(Thread.currentThread().getName() + " acquired LOCK_ONE");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Runnable taskOne = () -> {
            new Worker().doWork();
        };
        Runnable taskTwo = () -> {
            new Worker().doAnotherWork();
        };

        var t1 = new Thread(taskOne);
        var t2 = new Thread(taskTwo);

        t1.start();
        t2.start();
    }
}
