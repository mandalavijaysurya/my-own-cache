package org.example;

/**
 * @author: Vijaysurya Mandala
 * @github: github/mandalavijaysurya (<a href="https://www.github.com/mandalavijaysurya"> Github</a>)
 */
public class CacheItem<K, V> {
    private final K key;
    private final V value;
    private boolean stale;
    private final long expirationTime; // Expiration time in milliseconds

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public boolean isStale() {
        return stale;
    }
    public void setStale(){
        this.stale = true;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    @Override
    public String toString() {
        return "CacheItem{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", isStale=" + stale +
                ", expirationTime=" + expirationTime +
                '}';
    }

    public CacheItem(K key, V value, long ttl) {
        this.key = key;
        this.value = value;
        this.stale = false;
        this.expirationTime = System.currentTimeMillis() + ttl;
    }
}
