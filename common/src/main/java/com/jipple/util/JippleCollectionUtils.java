package com.jipple.util;

import com.google.common.collect.Ordering;

import java.util.*;

/**
 * Utility methods for collection operations.
 */
public class JippleCollectionUtils {
    /**
     * Same function as `keys.zipWithIndex.toMap`, but has perf gain.
     * Creates a map from keys to their indices (0-based).
     * 
     * @param keys the iterable of keys
     * @param <K> the type of keys
     * @return a map where each key is mapped to its index in the iterable
     */
    public static <K> Map<K, Integer> toMapWithIndex(Iterable<K> keys) {
        if (keys == null) {
            return Collections.emptyMap();
        }
        
        Map<K, Integer> result = new LinkedHashMap<>();
        Iterator<K> keyIter = keys.iterator();
        int idx = 0;
        while (keyIter.hasNext()) {
            result.put(keyIter.next(), idx);
            idx++;
        }
        return result;
    }

    /**
     * Same function as `keys.zip(values).toMap`, but has perf gain.
     * Creates a map by pairing corresponding elements from keys and values iterables.
     * 
     * @param keys the iterable of keys
     * @param values the iterable of values
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return a map where each key from keys is paired with the corresponding value from values
     */
    public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values) {
        if (keys == null || values == null) {
            return Collections.emptyMap();
        }
        
        Map<K, V> result = new LinkedHashMap<>();
        Iterator<K> keyIter = keys.iterator();
        Iterator<V> valueIter = values.iterator();
        while (keyIter.hasNext() && valueIter.hasNext()) {
            result.put(keyIter.next(), valueIter.next());
        }
        return result;
    }

    /**
     * Returns the first K elements from the input as defined by the specified Comparator
     * and maintains the ordering.
     * 
     * @param iterable the input iterable
     * @param num the number of elements to take
     * @param comparator the comparator to define the ordering
     * @param <T> the type of elements
     * @return an iterator containing the first num elements in the specified order
     */
    public static <T> List<T> takeOrdered(Iterable<T> iterable, int num, Comparator<T> comparator) {
        if (iterable == null || num <= 0) {
            return Collections.emptyList();
        }
        
        Ordering<T> ordering = Ordering.from(comparator);
        List<T> least = ordering.leastOf(iterable, num);
        return least;
    }
}
