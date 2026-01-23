package com.jipple.util;

import java.util.function.Supplier;

public class Utils {

    /**
     * Gets the context class loader or falls back to Jipple class loader.
     *
     * @return the context class loader or Jipple class loader
     */
    public static ClassLoader getContextOrJippleClassLoader() {
        return JippleClassUtils.getContextOrJippleClassLoader();
    }

    /**
     * Indicates whether Spark is currently running unit tests.
     */
    public static boolean isTesting() {
        return System.getenv("JIPPLE_TESTING") != null || System.getProperty("jipple.testing") != null;
    }

    /**
     * Tests an expression, throwing an AssertionError if false.
     */
    public static void assertCondition(boolean assertion) {
        if (!assertion) {
            throw new AssertionError("assertion failed");
        }
    }

    /**
     * Tests an expression, throwing an AssertionError if false.
     *
     * @param message a message to include in the failure message
     */
    public static void assertCondition(boolean assertion, Object message) {
        if (!assertion) {
            throw new AssertionError("assertion failed: " + message);
        }
    }

    public static void assertCondition(boolean assertion, Supplier<?> message) {
        if (!assertion) {
            throw new AssertionError("assertion failed: " + message.get());
        }
    }

}
