package dev.reed.core.threads._03092024.main.starvation;

import java.util.concurrent.TimeUnit;

/**
 * Your task is to implement a starvation using the thread priority mechanism.
 * The solution behavior should be as follows: one thread has a high priority, whereas another one
 * has a low priority, so the high-priority thread is stealing the work from the low-priority thread.
 */
public class ThreadPriorityStarvation {

    public static void main(String[] args) {
        Runnable lowPriorityTask = () -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + " is running");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
        };

        Runnable highPriorityTask = () -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + " is running");
            }
        };

        Thread lowPriorityThread = new Thread(lowPriorityTask, "Low-Priority-Thread");
        Thread highPriorityThread = new Thread(highPriorityTask, "High-Priority-Thread-1");

        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);
        highPriorityThread.setPriority(Thread.MAX_PRIORITY);

        lowPriorityThread.start();
        highPriorityThread.start();
    }
}
