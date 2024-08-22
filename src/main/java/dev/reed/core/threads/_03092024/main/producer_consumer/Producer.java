package dev.reed.core.threads._03092024.main.producer_consumer;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Please refer to the task description - {@link  dev.reed.core.threads._03092024.main.producer_consumer.task.md}
 */
@Slf4j
public class Producer implements Runnable {

    private static final int TEN_SECONDS_TIMEOUT_MILLIS = 10000;
    private static final int MAX_SIZE = 10;

    private int value;
    private final BlockingQueue<SyncItem> queue;

    public Producer(BlockingQueue<SyncItem> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        queue.notifyAll();
                        if (queue.size() >= MAX_SIZE) {
                            log.warn("Current queue size = {}, waiting 10 seconds and continue", queue.size());
                            queue.wait(TEN_SECONDS_TIMEOUT_MILLIS);
                        }
                    }
                }
                SyncItem item = SyncItem.builder()
                        .description("Simple description " + ++value)
                        .quantity(value)
                        .createdAt(LocalDateTime.now())
                        .build();
                queue.put(item);
                log.info("{} produced an item {}", Thread.currentThread().getName(), item);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }
}
