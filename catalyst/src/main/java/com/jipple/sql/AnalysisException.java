package com.jipple.sql;

import com.jipple.QueryContext;
import com.jipple.error.JippleThrowable;
import com.jipple.error.JippleThrowableHelper;
import com.jipple.sql.catalyst.trees.Origin;
import com.jipple.sql.catalyst.trees.WithOrigin;

import java.util.HashMap;
import java.util.Map;

/**
 * Thrown when a query fails to analyze, usually because the query itself is invalid.
 */
public class AnalysisException extends RuntimeException implements JippleThrowable, WithOrigin {
    
    private final String message;
    private final Integer line;
    private final Integer startPosition;
    private final Throwable cause;
    private final String errorClass;
    private final Map<String, String> messageParameters;
    private final QueryContext[] context;
    
    private Origin _origin;
    
    // Main constructor
    public AnalysisException(String message, Integer line, Integer startPosition,
                      Throwable cause, String errorClass,
                      Map<String, String> messageParameters, QueryContext[] context) {
        super(message, cause);
        this.message = message;
        this.line = line;
        this.startPosition = startPosition;
        this.cause = cause;
        this.errorClass = errorClass;
        this.messageParameters = messageParameters != null ? new HashMap<>(messageParameters) : new HashMap<>();
        this.context = context != null ? context : new QueryContext[0];
    }
    
    public AnalysisException(String errorClass, Map<String, String> messageParameters, Throwable cause) {
        this(
            JippleThrowableHelper.getMessage(errorClass, messageParameters),
            null,
            null,
            cause,
            errorClass,
            messageParameters,
            new QueryContext[0]
        );
    }
    
    public AnalysisException(String errorClass, Map<String, String> messageParameters,
                             QueryContext[] context, String summary) {
        this(
            JippleThrowableHelper.getMessage(errorClass, messageParameters, summary),
            null,
            null,
            null,
            errorClass,
            messageParameters,
            context
        );
    }
    
    public AnalysisException(String errorClass, Map<String, String> messageParameters) {
        this(
            errorClass,
            messageParameters,
            (Throwable) null
        );
    }
    
    public AnalysisException(String errorClass, Map<String, String> messageParameters, Origin origin) {
        this(
            JippleThrowableHelper.getMessage(errorClass, messageParameters),
            origin.line,
            origin.startPosition,
            null,
            errorClass,
            messageParameters,
            origin.getQueryContext()
        );
    }
    
    public AnalysisException(String errorClass, Map<String, String> messageParameters,
                             Origin origin, Throwable cause) {
        this(
            JippleThrowableHelper.getMessage(errorClass, messageParameters),
            origin.line,
            origin.startPosition,
            cause,
            errorClass,
            messageParameters,
            origin.getQueryContext()
        );
    }
    
    /**
     * Creates a copy of this exception with updated fields.
     * 
     * @param message the message
     * @param line the line number
     * @param startPosition the start position
     * @param cause the cause
     * @param errorClass the error class
     * @param messageParameters the message parameters
     * @param context the query context
     * @return a new AnalysisException2 with the specified fields
     */
    public AnalysisException copy(String message, Integer line, Integer startPosition,
                                  Throwable cause, String errorClass,
                                  Map<String, String> messageParameters, QueryContext[] context) {
        return new AnalysisException(
            message != null ? message : this.message,
            line != null ? line : this.line,
            startPosition != null ? startPosition : this.startPosition,
            cause != null ? cause : this.cause,
            errorClass != null ? errorClass : this.errorClass,
            messageParameters != null ? messageParameters : this.messageParameters,
            context != null ? context : this.context
        );
    }
    
    /**
     * Creates a copy with updated position from the given origin.
     * 
     * @param origin the origin to get position from
     * @return a new AnalysisException2 with updated position
     */
    public AnalysisException withPosition(Origin origin) {
        AnalysisException newException = this.copy(
            null,
            origin.line,
            origin.startPosition,
            null,
            null,
            null,
            origin.getQueryContext()
        );
        newException.setStackTrace(getStackTrace());
        return newException;
    }
    
    @Override
    public String getMessage() {
        return getSimpleMessage();
    }
    
    /**
     * Outputs an exception without the logical plan.
     * For testing only
     * 
     * @return the simple message
     */
    public String getSimpleMessage() {
        if (line != null || startPosition != null) {
            String lineAnnotation = line != null ? " line " + line : "";
            String positionAnnotation = startPosition != null ? " pos " + startPosition : "";
            return message + ";" + lineAnnotation + positionAnnotation;
        } else {
            return message;
        }
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
    
    @Override
    public Origin origin() {
        if (_origin == null) {
            _origin = new Origin(line, startPosition, null, null, null, null, null);
        }
        return _origin;
    }
}

