package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: Vijaysurya Mandala
 * @github: github/mandalavijaysurya (<a href="https://www.github.com/mandalavijaysurya"> Github</a>)
 */
public class LruCache<K,V>{
    private final ConcurrentHashMap<K, CacheItem<K,V>> map;
    private final ConcurrentLinkedDeque<CacheItem<K,V>> deque;
    private final long ttl;
    private final int capacity;
    private final ReentrantLock lock;

    public LruCache(int capacity, long ttl){
        this.capacity = capacity;
        this.ttl = ttl;
        this.map = new ConcurrentHashMap<>();
        this.deque = new ConcurrentLinkedDeque<>();
        this.lock = new ReentrantLock();

        startInvalidationThread();
        startEvictionThread();
    }
    public Map<K, CacheItem<K,V>> getMap(){
        return map;
    }
    public void put(K key, V value){
        lock.lock();
        try {
            if(map.containsKey(key)){
                CacheItem<K,V> existingItem = map.get(key);
                deque.remove(existingItem);
            }else if(capacity <= map.size()){
                evict();
            }
            CacheItem<K,V> item = new CacheItem<>(key, value, ttl);
            deque.addFirst(item);
            map.put(key, item);
        }finally {
            lock.unlock();
        }
    }

    public V get(String key){
        lock.lock();
        try{
            CacheItem<K,V> item = map.get(key);
            if(item == null || item.isStale()){
                return null;
            }
            //As accessing adding at front of the queue
            deque.remove(item);
            deque.addFirst(item);
            return item.getValue();
        }finally {
            lock.unlock();
        }
    }

    public V remove(String key){
        lock.lock();
        try{
            CacheItem<K,V> item = map.remove(key);
            if(item == null)
                throw new RuntimeException("Element is not found in Cache.");
            deque.remove(item);
            return item.getValue();
        }finally {
            lock.unlock();
        }
    }
    private void evict() {
        lock.lock();
        try {
            CacheItem<K,V> evicted = deque.pollLast();
            if (evicted != null) {
                map.remove(evicted.getKey());
            }
        } finally {
            lock.unlock();
        }
    }

    private void startInvalidationThread() {
        Thread invalidationThread = new Thread(() -> {
            while (true) {
                try {
                    lock.lock();
                    try {
                        long now = System.currentTimeMillis();
                        for (CacheItem<K,V> item : deque) {
                            if (item.getExpirationTime() <= now) {
                                item.setStale();
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                    Thread.sleep(1000); // Sleep for a while before the next check
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Invalidation-Thread");
        invalidationThread.setDaemon(true);
        invalidationThread.start();
    }

    private void startEvictionThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    lock.lock();
                    try {
                        CacheItem<K,V> item = deque.peekLast();
                        if (item != null && item.isStale()) {
                            evict();
                        }
                    } finally {
                        lock.unlock();
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException exception){
                    Thread.currentThread().interrupt();
                }
            }
        }, "Eviction-Thread");
        thread.setDaemon(true);
        thread.start();
    }

}
