package com.jipple.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * SPARK-43300: Guava cache fate-sharing behavior might lead to unexpected cascade failure:
 * when multiple threads access the same key in the cache at the same time when the key is not in
 * the cache, Guava cache will block all requests and load the data only once. If the loading fails,
 * all requests will fail immediately without retry. Therefore individual failure will also fail
 * other irrelevant queries who are waiting for the same key. Given that spark can cancel tasks at
 * arbitrary times for many different reasons, fate sharing means that a task which gets canceled
 * while populating a cache entry can cause spurious failures in tasks from unrelated jobs -- even
 * though those tasks would have successfully populated the cache if they had been allowed to try.
 *
 * This util Cache wrapper with KeyLock to synchronize threads looking for the same key
 * so that they should run individually and fail as if they had arrived one at a time.
 *
 * There are so many ways to add cache entries in Guava Cache, instead of implementing Guava Cache
 * and LoadingCache interface, we expose a subset of APIs so that we can control at compile time
 * what cache operations are allowed.
 */
public class NonFateSharingCache<K, V> {
    protected final Cache<K, V> cache;
    protected final KeyLock<K> keyLock;

    /**
     * Creates a NonFateSharingCache from a Guava Cache.
     * If the cache is a LoadingCache, returns a NonFateSharingLoadingCache instead.
     */
    public static <K, V> NonFateSharingCache<K, V> create(Cache<K, V> cache) {
        if (cache instanceof LoadingCache) {
            return new NonFateSharingLoadingCache<>((LoadingCache<K, V>) cache);
        } else {
            return new NonFateSharingCache<>(cache);
        }
    }

    /**
     * Creates a NonFateSharingLoadingCache from a Guava LoadingCache.
     */
    public static <K, V> NonFateSharingLoadingCache<K, V> create(LoadingCache<K, V> loadingCache) {
        return new NonFateSharingLoadingCache<>(loadingCache);
    }

    /**
     * SPARK-44064 add this `create` function to break non-core modules code directly using
     * Guava Cache related types as input parameter to invoke other `NonFateSharingCache#create`
     * function, which can avoid non-core modules Maven test failures caused by using
     * shaded core module.
     * We should refactor this function to be more general when there are other requirements,
     * or remove this function when Maven testing is no longer supported.
     * 
     * @param loadingFunc the function to load values for keys
     * @param maximumSize the maximum size of the cache (0 means unlimited)
     * @return a NonFateSharingLoadingCache instance
     */
    public static <K, V> NonFateSharingLoadingCache<K, V> create(
            Function<K, V> loadingFunc, long maximumSize) {
        if (loadingFunc == null) {
            throw new IllegalArgumentException("loadingFunc cannot be null");
        }
        @SuppressWarnings("unchecked")
        CacheBuilder<K, V> builder = (CacheBuilder<K, V>) CacheBuilder.newBuilder();
        if (maximumSize > 0L) {
            builder.maximumSize(maximumSize);
        }
        LoadingCache<K, V> loadingCache = builder.build(new CacheLoader<K, V>() {
            @Override
            public V load(K k) {
                return loadingFunc.apply(k);
            }
        });
        return new NonFateSharingLoadingCache<>(loadingCache);
    }

    /**
     * Creates a NonFateSharingLoadingCache with default maximum size (unlimited).
     */
    public static <K, V> NonFateSharingLoadingCache<K, V> create(Function<K, V> loadingFunc) {
        return create(loadingFunc, 0L);
    }

    /**
     * Protected constructor for subclasses.
     */
    protected NonFateSharingCache(Cache<K, V> cache) {
        this.cache = cache;
        this.keyLock = new KeyLock<>();
    }

    /**
     * Returns the value associated with key in this cache, obtaining that value from
     * valueLoader if necessary. No observable state associated with this cache is modified
     * until loading completes. This method provides a simple substitute for the conventional
     * "if cached, return; otherwise create, cache and return" pattern.
     *
     * @param key the key to look up
     * @param valueLoader the callable to load the value if not present
     * @return the value associated with key
     * @throws RuntimeException if the valueLoader throws an exception
     */
    public V get(K key, Callable<? extends V> valueLoader) {
        return keyLock.withLock(key, () -> {
            try {
                return cache.get(key, valueLoader);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Returns the value associated with key in this cache, or null if there is no cached value
     * for that key.
     *
     * @param key the key to look up
     * @return the value associated with key, or null if not present
     */
    public V getIfPresent(Object key) {
        // Guava Cache.getIfPresent returns Object, but the generic type ensures type safety
        return cache.getIfPresent(key);
    }

    /**
     * Discards any cached value for key key.
     *
     * @param key the key to invalidate
     */
    public void invalidate(Object key) {
        cache.invalidate(key);
    }

    /**
     * Discards all entries in the cache.
     */
    public void invalidateAll() {
        cache.invalidateAll();
    }

    /**
     * Returns the approximate number of entries in this cache.
     *
     * @return the number of entries
     */
    public long size() {
        return cache.size();
    }
}

