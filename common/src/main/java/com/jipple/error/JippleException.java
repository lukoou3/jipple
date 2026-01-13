package com.jipple.error;

import com.jipple.QueryContext;

import java.util.*;

/**
 * Base exception class for Jipple exceptions.
 */
public class JippleException extends RuntimeException implements JippleThrowable {
    
    private final String errorClass;
    private final Map<String, String> messageParameters;
    private final QueryContext[] context;
    
    public JippleException(String message, Throwable cause, String errorClass, 
                          Map<String, String> messageParameters, QueryContext[] context) {
        super(message, cause);
        this.errorClass = errorClass;
        this.messageParameters = messageParameters != null ? new HashMap<>(messageParameters) : new HashMap<>();
        this.context = context != null ? context : new QueryContext[0];
    }
    
    public JippleException(String message, Throwable cause) {
        this(message, cause, null, new HashMap<>(), new QueryContext[0]);
    }
    
    public JippleException(String message) {
        this(message, null);
    }
    
    public JippleException(String errorClass, Map<String, String> messageParameters, 
                          Throwable cause, QueryContext[] context, String summary) {
        this(
            JippleThrowableHelper.getMessage(errorClass, messageParameters, summary),
            cause,
            errorClass,
            messageParameters,
            context
        );
    }
    
    public JippleException(String errorClass, Map<String, String> messageParameters, Throwable cause) {
        this(
            JippleThrowableHelper.getMessage(errorClass, messageParameters),
            cause,
            errorClass,
            messageParameters,
            new QueryContext[0]
        );
    }
    
    public JippleException(String errorClass, Map<String, String> messageParameters, 
                          Throwable cause, QueryContext[] context) {
        this(
            JippleThrowableHelper.getMessage(errorClass, messageParameters),
            cause,
            errorClass,
            messageParameters,
            context
        );
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
    
    // Static factory methods
    public static JippleException internalError(String msg, QueryContext[] context, String summary) {
        return internalError(msg, context, summary, null);
    }
    
    public static JippleException internalError(String msg, QueryContext[] context, 
                                                String summary, String category) {
        String errorClass = "INTERNAL_ERROR" + (category != null ? "_" + category : "");
        Map<String, String> params = new HashMap<>();
        params.put("message", msg);
        return new JippleException(errorClass, params, null, context, summary);
    }
    
    public static JippleException internalError(String msg) {
        return internalError(msg, new QueryContext[0], "", null);
    }
    
    public static JippleException internalError(String msg, String category) {
        return internalError(msg, new QueryContext[0], "", category);
    }
    
    public static JippleException internalError(String msg, Throwable cause) {
        Map<String, String> params = new HashMap<>();
        params.put("message", msg);
        return new JippleException("INTERNAL_ERROR", params, cause);
    }
}
