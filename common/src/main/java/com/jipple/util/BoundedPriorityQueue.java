package com.jipple.util;

import java.io.Serializable;
import java.util.*;

/**
 * Bounded priority queue. This class wraps the original PriorityQueue
 * class and modifies it such that only the top K elements are retained.
 * The top K elements are defined by a Comparator.
 */
public class BoundedPriorityQueue<A> implements Iterable<A>, Serializable {
    
    private final PriorityQueue<A> underlying;
    private final int maxSize;
    private final Comparator<A> comparator;

    /**
     * Creates a bounded priority queue with the specified maximum size and comparator.
     *
     * @param maxSize the maximum number of elements to retain
     * @param comparator the comparator to determine the ordering of elements
     */
    public BoundedPriorityQueue(int maxSize, Comparator<A> comparator) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }
        this.maxSize = maxSize;
        this.comparator = comparator;
        this.underlying = new PriorityQueue<>(maxSize, comparator);
    }

    /**
     * Creates a bounded priority queue with the specified maximum size.
     * Uses natural ordering (requires A to implement Comparable).
     *
     * @param maxSize the maximum number of elements to retain
     */
    public BoundedPriorityQueue(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }
        this.maxSize = maxSize;
        this.comparator = null;
        this.underlying = new PriorityQueue<>(maxSize);
    }

    @Override
    public Iterator<A> iterator() {
        return underlying.iterator();
    }

    /**
     * Returns the number of elements in this queue.
     */
    public int size() {
        return underlying.size();
    }

    /**
     * Adds all elements from the given collection to this queue.
     *
     * @param xs the collection of elements to add
     * @return this queue for method chaining
     */
    public BoundedPriorityQueue<A> addAll(Iterable<A> xs) {
        for (A x : xs) {
            add(x);
        }
        return this;
    }

    /**
     * Adds a single element to this queue.
     *
     * @param elem the element to add
     * @return this queue for method chaining
     */
    public BoundedPriorityQueue<A> add(A elem) {
        if (size() < maxSize) {
            underlying.offer(elem);
        } else {
            maybeReplaceLowest(elem);
        }
        return this;
    }

    /**
     * Adds multiple elements to this queue.
     *
     * @param elem1 the first element
     * @param elem2 the second element
     * @param elems additional elements
     * @return this queue for method chaining
     */
    @SafeVarargs
    public final BoundedPriorityQueue<A> add(A elem1, A elem2, A... elems) {
        add(elem1);
        add(elem2);
        for (A elem : elems) {
            add(elem);
        }
        return this;
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue, or null if empty
     */
    public A poll() {
        return underlying.poll();
    }

    /**
     * Removes all elements from this queue.
     */
    public void clear() {
        underlying.clear();
    }

    /**
     * Attempts to replace the lowest element if the new element is greater.
     * Returns true if replacement occurred, false otherwise.
     */
    private boolean maybeReplaceLowest(A a) {
        A head = underlying.peek();
        if (head != null && compare(a, head) > 0) {
            underlying.poll();
            underlying.offer(a);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Compares two elements using the comparator or natural ordering.
     */
    private int compare(A a, A b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            return ((Comparable<A>) a).compareTo(b);
        }
    }
}

