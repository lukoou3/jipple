package com.jipple.error;

import java.util.HashMap;
import java.util.Map;

/**
 * Security exception thrown from Jipple with an error class.
 */
public class JippleSecurityException extends SecurityException implements JippleThrowable {
    
    private final String errorClass;
    private final Map<String, String> messageParameters;
    
    public JippleSecurityException(String errorClass, Map<String, String> messageParameters) {
        super(JippleThrowableHelper.getMessage(errorClass, messageParameters));
        this.errorClass = errorClass;
        this.messageParameters = messageParameters != null ? new HashMap<>(messageParameters) : new HashMap<>();
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

