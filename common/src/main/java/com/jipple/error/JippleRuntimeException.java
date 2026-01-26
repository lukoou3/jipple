package com.jipple.error;

import com.jipple.QueryContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Runtime exception thrown from Jipple with an error class.
 */
public class JippleRuntimeException extends RuntimeException implements JippleThrowable {
    
    private final String errorClass;
    private final Map<String, String> messageParameters;
    private final QueryContext[] context;
    
    public JippleRuntimeException(String errorClass, Map<String, String> messageParameters,
                                 Throwable cause, QueryContext[] context, String summary) {
        super(JippleThrowableHelper.getMessage(errorClass, messageParameters, summary), cause);
        this.errorClass = errorClass;
        this.messageParameters = messageParameters != null ? new HashMap<>(messageParameters) : new HashMap<>();
        this.context = context != null ? context : new QueryContext[0];
    }

    public JippleRuntimeException(String errorClass, Map<String, String> messageParameters, Throwable cause) {
        this(errorClass, messageParameters, cause, new QueryContext[0], "");
    }

    public JippleRuntimeException(String errorClass, Map<String, String> messageParameters) {
        this(errorClass, messageParameters, null, new QueryContext[0], "");
    }
    
    public JippleRuntimeException(String message, Throwable cause) {
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

