package com.jipple.util;

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

}
