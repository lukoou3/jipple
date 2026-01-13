package com.jipple.error;

import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;

/**
 * SQL feature not supported exception thrown from Jipple with an error class.
 */
public class JippleSQLFeatureNotSupportedException extends SQLFeatureNotSupportedException implements JippleThrowable {
    
    private final String errorClass;
    private final Map<String, String> messageParameters;
    
    public JippleSQLFeatureNotSupportedException(String errorClass, Map<String, String> messageParameters) {
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

