package com.jipple.error;

/**
 * Exception thrown when execution of some user code in the driver process fails, e.g.
 * accumulator update fails or failure in takeOrdered (user supplies an Ordering implementation
 * that can be misbehaving.
 */
public class JippleDriverExecutionException extends JippleException {
    public JippleDriverExecutionException(Throwable cause) {
        super("Execution error", cause);
    }
}

