package com.jipple.error;

import java.util.Collections;

/**
 * Exception thrown when the relative executor to access is dead.
 */
public class ExecutorDeadException extends JippleException {
    public ExecutorDeadException(String message) {
        super("INTERNAL_ERROR_NETWORK", 
              Collections.singletonMap("message", message), 
              null);
    }
}

