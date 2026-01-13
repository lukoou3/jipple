package com.jipple.error;

import com.jipple.QueryContext;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * No such element exception thrown from Jipple with an error class.
 */
public class JippleNoSuchElementException extends NoSuchElementException implements JippleThrowable {
    
    private final String errorClass;
    private final Map<String, String> messageParameters;
    private final QueryContext[] context;
    
    public JippleNoSuchElementException(String errorClass, Map<String, String> messageParameters,
                                      QueryContext[] context, String summary) {
        super(JippleThrowableHelper.getMessage(errorClass, messageParameters, summary));
        this.errorClass = errorClass;
        this.messageParameters = messageParameters != null ? new HashMap<>(messageParameters) : new HashMap<>();
        this.context = context != null ? context : new QueryContext[0];
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

