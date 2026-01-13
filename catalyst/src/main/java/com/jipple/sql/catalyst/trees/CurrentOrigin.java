package com.jipple.sql.catalyst.trees;

import java.util.function.Supplier;

/**
 * Provides a location for TreeNodes to ask about the context of their origin. For example, which
 * line of code is currently being parsed.
 */
public class CurrentOrigin {

    private static final ThreadLocal<Origin> value = new ThreadLocal<Origin>() {
        @Override
        protected Origin initialValue() {
            return new Origin();
        }
    };

    private CurrentOrigin() {
        // Utility class, prevent instantiation
    }

    /**
     * Gets the current origin.
     *
     * @return the current origin
     */
    public static Origin get() {
        return value.get();
    }

    /**
     * Sets the current origin.
     *
     * @param o the origin to set
     */
    public static void set(Origin o) {
        value.set(o);
    }

    /**
     * Resets the current origin to an empty origin.
     */
    public static void reset() {
        value.set(new Origin());
    }

    /**
     * Sets the position of the current origin.
     *
     * @param line the line number
     * @param start the start position
     */
    public static void setPosition(int line, int start) {
        Origin current = get();
        set(current.copy(line, start));
    }

    /**
     * Executes a function with a specific origin, restoring the previous origin afterwards.
     * This allows withOrigin to be called recursively.
     *
     * @param o the origin to use
     * @param f the function to execute
     * @param <A> the return type
     * @return the result of the function
     */
    public static <A> A withOrigin(Origin o, Supplier<A> f) {
        // remember the previous one so it can be reset to this
        // this way withOrigin can be recursive
        Origin previous = get();
        set(o);
        try {
            return f.get();
        } finally {
            set(previous);
        }
    }
}


