package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world!");
        LruCache<String, String> lruTtlCache = new LruCache<>(10000, 10000);
        lruTtlCache.put("ASF01ASFUO0123","Vijaysurya");
        for(int i = 0; i < 11; i++){
            Thread.sleep(1000);
            System.out.println(lruTtlCache.getMap());
        }

    }
}