package com.jipple.sql.catalyst.util;

/**
 * Utility class for SQL ordering operations.
 * Provides special comparison methods that follow SQL semantics.
 */
public final class SQLOrderingUtil {

    private SQLOrderingUtil() {
        // Utility class, prevent instantiation
    }

    /**
     * A special version of double comparison that follows SQL semantic:
     * <ul>
     *   <li>NaN == NaN</li>
     *   <li>NaN is greater than any non-NaN double</li>
     *   <li>-0.0 == 0.0</li>
     * </ul>
     *
     * @param x the first double to compare
     * @param y the second double to compare
     * @return a negative integer, zero, or a positive integer as the first argument
     *         is less than, equal to, or greater than the second
     */
    public static int compareDoubles(double x, double y) {
        if (x == y) {
            return 0;
        } else {
            return Double.compare(x, y);
        }
    }

    /**
     * A special version of float comparison that follows SQL semantic:
     * <ul>
     *   <li>NaN == NaN</li>
     *   <li>NaN is greater than any non-NaN float</li>
     *   <li>-0.0 == 0.0</li>
     * </ul>
     *
     * @param x the first float to compare
     * @param y the second float to compare
     * @return a negative integer, zero, or a positive integer as the first argument
     *         is less than, equal to, or greater than the second
     */
    public static int compareFloats(float x, float y) {
        if (x == y) {
            return 0;
        } else {
            return Float.compare(x, y);
        }
    }
}

