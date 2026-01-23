package com.jipple.util;

import com.google.common.cache.LoadingCache;

/**
 * A NonFateSharingCache that wraps a LoadingCache, providing a get method that uses
 * the cache's loader function.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class NonFateSharingLoadingCache<K, V> extends NonFateSharingCache<K, V> {
    protected final LoadingCache<K, V> loadingCache;

    /**
     * Creates a NonFateSharingLoadingCache from a Guava LoadingCache.
     *
     * @param loadingCache the LoadingCache to wrap
     */
    public NonFateSharingLoadingCache(LoadingCache<K, V> loadingCache) {
        super(loadingCache);
        this.loadingCache = loadingCache;
    }

    /**
     * Returns the value associated with key in this cache, first loading that value if necessary.
     * No observable state associated with this cache is modified until loading completes.
     *
     * @param key the key to look up
     * @return the value associated with key
     * @throws RuntimeException if the cache loader throws an exception
     */
    public V get(K key) {
        return keyLock.withLock(key, () -> {
            try {
                return loadingCache.get(key);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

