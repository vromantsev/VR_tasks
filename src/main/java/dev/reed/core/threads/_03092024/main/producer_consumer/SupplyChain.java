package dev.reed.core.threads._03092024.main.producer_consumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Please refer to the task description - {@link  dev.reed.core.threads._03092024.main.producer_consumer.task.md}
 */
public class SupplyChain {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<SyncItem> queue = new ArrayBlockingQueue<>(10);
        Producer producer = new Producer(queue);
        Consumer consumerOne = new Consumer(queue);
        Consumer consumerTwo = new Consumer(queue);

        EXECUTOR_SERVICE.submit(producer);
        EXECUTOR_SERVICE.submit(consumerOne);
        EXECUTOR_SERVICE.submit(consumerTwo);
    }
}
