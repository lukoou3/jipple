package com.jipple.util;

public class Utils {

    /**
     * Indicates whether Spark is currently running unit tests.
     */
    public static boolean isTesting() {
        return System.getenv("JIPPLE_TESTING") != null || System.getProperty("jipple.testing") != null;
    }

}
