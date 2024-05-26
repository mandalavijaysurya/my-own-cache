# LRU and TTL Cache
This project implements a thread-safe cache with both Time-To-Live (TTL) based invalidation and Least Recently Used (LRU) based eviction policies using Java. The cache is designed to operate in a multithreaded environment, ensuring data consistency and optimal resource usage.

## Features
#### TTL (Time-To-Live) Invalidation:
Cache entries are marked as stale after a specified TTL and are removed by the invalidation thread.
#### LRU Eviction:
When the cache reaches its capacity, the least recently used items are evicted to make room for new entries.
#### Thread Safety: 
Utilizes ConcurrentHashMap and ConcurrentLinkedDeque for thread-safe operations and a ReentrantLock to manage critical sections.