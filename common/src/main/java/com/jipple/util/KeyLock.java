package com.jipple.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A special locking mechanism to provide locking with a given key. By providing the same key
 * (identity is tested using the `equals` method), we ensure there is only one `func` running at
 * the same time.
 *
 * @param <K> the type of key to identify a lock. This type must implement `equals` and `hashCode`
 *           correctly as it will be the key type of an internal Map.
 */
public class KeyLock<K> {

    private final ConcurrentHashMap<K, Object> lockMap = new ConcurrentHashMap<>();

    /**
     * Acquires a lock for the given key. This method will block until the lock is acquired.
     * The lock is identified by the key's equals method.
     *
     * @param key the key to acquire the lock for
     */
    private void acquireLock(K key) {
        while (true) {
            Object lock = lockMap.putIfAbsent(key, new Object());
            if (lock == null) {
                return; // Successfully acquired the lock
            }
            // Another thread is holding the lock, wait for it
            synchronized (lock) {
                // Double-check: make sure the lock is still in the map
                while (lockMap.get(key) == lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while waiting for lock", e);
                    }
                }
            }
        }
    }

    /**
     * Releases the lock for the given key and notifies waiting threads.
     *
     * @param key the key to release the lock for
     */
    private void releaseLock(K key) {
        Object lock = lockMap.remove(key);
        if (lock != null) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    /**
     * Run `func` under a lock identified by the given key. Multiple calls with the same key
     * (identity is tested using the `equals` method) will be locked properly to ensure there is only
     * one `func` running at the same time.
     *
     * @param key the key to identify the lock
     * @param func the function to execute under the lock
     * @param <T> the return type of the function
     * @return the result of the function
     * @throws NullPointerException if key is null
     */
    public <T> T withLock(K key, Supplier<T> func) {
        if (key == null) {
            throw new NullPointerException("key must not be null");
        }
        acquireLock(key);
        try {
            return func.get();
        } finally {
            releaseLock(key);
        }
    }

    /**
     * Run `runnable` under a lock identified by the given key. Multiple calls with the same key
     * (identity is tested using the `equals` method) will be locked properly to ensure there is only
     * one `runnable` running at the same time.
     *
     * @param key the key to identify the lock
     * @param runnable the code to execute under the lock
     * @throws NullPointerException if key is null
     */
    public void withLock(K key, Runnable runnable) {
        if (key == null) {
            throw new NullPointerException("key must not be null");
        }
        acquireLock(key);
        try {
            runnable.run();
        } finally {
            releaseLock(key);
        }
    }
}

