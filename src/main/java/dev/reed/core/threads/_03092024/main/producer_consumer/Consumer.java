package dev.reed.core.threads._03092024.main.producer_consumer;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Consumer implements Runnable {

    private final BlockingQueue<SyncItem> queue;

    public Consumer(BlockingQueue<SyncItem> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (queue) {
                    if (queue.isEmpty()) {
                        queue.wait();
                    } else {
                        SyncItem item = this.queue.poll();
                        log.info("{} consumed sync item {}", Thread.currentThread().getName(), item);
                    }
                }
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }
}
