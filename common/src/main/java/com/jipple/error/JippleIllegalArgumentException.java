package com.jipple.error;

import com.jipple.QueryContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Illegal argument exception thrown from Jipple with an error class.
 */
public class JippleIllegalArgumentException extends IllegalArgumentException implements JippleThrowable {
    
    private final String errorClass;
    private final Map<String, String> messageParameters;
    private final QueryContext[] context;
    
    public JippleIllegalArgumentException(String errorClass, Map<String, String> messageParameters,
                                         QueryContext[] context, String summary, Throwable cause) {
        super(JippleThrowableHelper.getMessage(errorClass, messageParameters, summary), cause);
        this.errorClass = errorClass;
        this.messageParameters = messageParameters != null ? new HashMap<>(messageParameters) : new HashMap<>();
        this.context = context != null ? context : new QueryContext[0];
    }
    
    public JippleIllegalArgumentException(String errorClass, Map<String, String> messageParameters,
                                         QueryContext[] context, String summary) {
        this(errorClass, messageParameters, context, summary, null);
    }
    
    public JippleIllegalArgumentException(String errorClass, Map<String, String> messageParameters) {
        this(errorClass, messageParameters, new QueryContext[0], "", null);
    }
    
    public JippleIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
        this.errorClass = null;
        this.messageParameters = new HashMap<>();
        this.context = new QueryContext[0];
    }
    
    @Override
    public Map<String, String> getMessageParameters() {
        return new HashMap<>(messageParameters);
    }
    
    @Override
    public String getErrorClass() {
        return errorClass;
    }
    
    @Override
    public QueryContext[] getQueryContext() {
        return context;
    }
}

