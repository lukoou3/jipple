package com.jipple.error;

import java.util.HashMap;
import java.util.Map;

/**
 * Class not found exception thrown from Jipple with an error class.
 */
public class JippleClassNotFoundException extends ClassNotFoundException implements JippleThrowable {
    
    private final String errorClass;
    private final Map<String, String> messageParameters;
    
    public JippleClassNotFoundException(String errorClass, Map<String, String> messageParameters, Throwable cause) {
        super(JippleThrowableHelper.getMessage(errorClass, messageParameters), cause);
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

