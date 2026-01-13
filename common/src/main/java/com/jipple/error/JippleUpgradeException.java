package com.jipple.error;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when Jipple returns different result after upgrading to a new version.
 */
public class JippleUpgradeException extends RuntimeException implements JippleThrowable {
    
    private final String errorClass;
    private final Map<String, String> messageParameters;
    
    public JippleUpgradeException(String errorClass, Map<String, String> messageParameters, Throwable cause) {
        super(JippleThrowableHelper.getMessage(errorClass, messageParameters), cause);
        this.errorClass = errorClass;
        this.messageParameters = messageParameters != null ? new HashMap<>(messageParameters) : new HashMap<>();
    }
    
    public JippleUpgradeException(String message, Throwable cause) {
        super(message, cause);
        this.errorClass = null;
        this.messageParameters = new HashMap<>();
    }
    
    @Override
    public Map<String, String> getMessageParameters() {
        return new HashMap<>(messageParameters);
    }
    
    @Override
    public String getErrorClass() {
        return errorClass;
    }
}

