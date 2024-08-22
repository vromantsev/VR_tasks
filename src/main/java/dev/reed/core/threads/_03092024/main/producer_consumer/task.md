Objective:
You are tasked to implement a classic producer-consumer pattern. 

Description:
1 producer, 2 consumers.
It's desirable to use java.util.concurrent.ArrayBlockingQueue to simplify low-level code, but it is still up to you
to choose the tools you use.
Please note, that queue acts as a shared resource here.

Behavior:
Producer can produce items as fast as one per second, when consumer can process only 1 item per 3 seconds.
If the queue is not empty, producer must notify consumers, that they can consume the items.
If the queue reaches the size >= 10, then make producer wait for 10 seconds and continue automatically.
Consumer pull the items from a queue every 3 seconds till the moment it becomes empty, and once that happened, 
consumer should wait until the items appeared in the queue (e.g. be notified by producer).
