package com.jipple.error;

import com.jipple.QueryContext;
import com.jipple.util.JsonUtils;
import com.jipple.util.JippleClassUtils;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.*;

/**
 * Companion object used by instances of {@link JippleThrowable} to access error class information and
 * construct error messages.
 */
public class JippleThrowableHelper {
    
    private static final ErrorClassesJsonReader errorReader = new ErrorClassesJsonReader(Collections.singletonList(JippleClassUtils.getJippleClassLoader().getResource("error/error-classes.json")));


    /**
     * Gets the error message for the given error class and message parameters.
     * 
     * @param errorClass the error class
     * @param messageParameters the message parameters
     * @return the formatted error message
     */
    public static String getMessage(String errorClass, Map<String, String> messageParameters) {
        return getMessage(errorClass, messageParameters, "");
    }
    
    /**
     * Gets the error message for the given error class, message parameters, and context.
     * 
     * @param errorClass the error class
     * @param messageParameters the message parameters
     * @param context the query context string
     * @return the formatted error message
     */
    public static String getMessage(String errorClass, Map<String, String> messageParameters, String context) {
        String displayMessage = errorReader.getErrorMessage(errorClass, messageParameters);
        String displayQueryContext = (context.isEmpty() ? "" : "\n") + context;
        String prefix = errorClass.startsWith("_LEGACY_ERROR_TEMP_") ? "" : "[" + errorClass + "] ";
        return prefix + displayMessage + displayQueryContext;
    }
    
    /**
     * Gets the SQL state for the given error class.
     * 
     * @param errorClass the error class
     * @return the SQL state, or null if not found
     */
    public static String getSqlState(String errorClass) {
        return errorReader.getSqlState(errorClass);
    }
    
    /**
     * Checks if the error class represents an internal error.
     * 
     * @param errorClass the error class
     * @return true if it's an internal error, false otherwise
     */
    public static boolean isInternalError(String errorClass) {
        return errorClass != null && errorClass.startsWith("INTERNAL_ERROR");
    }
    
    /**
     * Gets the formatted error message for the given throwable and format.
     * 
     * @param e the throwable (must implement JippleThrowable)
     * @param format the message format
     * @return the formatted error message
     */
    public static String getMessage(Throwable e, ErrorMessageFormat format) {
        if (!(e instanceof JippleThrowable)) {
            throw new IllegalArgumentException("Throwable must implement JippleThrowable");
        }
        JippleThrowable jt = (JippleThrowable) e;
        
        if (format == ErrorMessageFormat.PRETTY) {
            return e.getMessage();
        }
        
        if ((format == ErrorMessageFormat.MINIMAL || format == ErrorMessageFormat.STANDARD) 
                && jt.getErrorClass() == null) {
            return JsonUtils.toJsonString(generator -> {
                try {
                    JsonGenerator g = generator.useDefaultPrettyPrinter();
                    g.writeStartObject();
                    g.writeStringField("errorClass", "LEGACY");
                    g.writeObjectFieldStart("messageParameters");
                    g.writeStringField("message", e.getMessage());
                    g.writeEndObject();
                    g.writeEndObject();
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to generate JSON", ex);
                }
            });
        }
        
        // MINIMAL | STANDARD with error class
        String errorClass = jt.getErrorClass();
        return JsonUtils.toJsonString(generator -> {
            try {
                JsonGenerator g = generator.useDefaultPrettyPrinter();
                g.writeStartObject();
                g.writeStringField("errorClass", errorClass);
                if (format == ErrorMessageFormat.STANDARD) {
                    g.writeStringField("messageTemplate", errorReader.getMessageTemplate(errorClass));
                }
                String sqlState = jt.getSqlState();
                if (sqlState != null) {
                    g.writeStringField("sqlState", sqlState);
                }
                Map<String, String> messageParameters = jt.getMessageParameters();
                if (messageParameters != null && !messageParameters.isEmpty()) {
                    g.writeObjectFieldStart("messageParameters");
                    // Convert to Map to remove duplicates, sort by key
                    messageParameters.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> {
                                try {
                                    String value = entry.getValue().replaceAll("#\\d+", "#x");
                                    g.writeStringField(entry.getKey(), value);
                                } catch (IOException ex) {
                                    throw new RuntimeException("Failed to write JSON field", ex);
                                }
                            });
                    g.writeEndObject();
                }
                QueryContext[] queryContext = jt.getQueryContext();
                if (queryContext != null && queryContext.length > 0) {
                    g.writeArrayFieldStart("queryContext");
                    for (QueryContext c : queryContext) {
                        g.writeStartObject();
                        g.writeStringField("objectType", c.objectType());
                        g.writeStringField("objectName", c.objectName());
                        int startIndex = c.startIndex() + 1;
                        if (startIndex > 0) {
                            g.writeNumberField("startIndex", startIndex);
                        }
                        int stopIndex = c.stopIndex() + 1;
                        if (stopIndex > 0) {
                            g.writeNumberField("stopIndex", stopIndex);
                        }
                        g.writeStringField("fragment", c.fragment());
                        g.writeEndObject();
                    }
                    g.writeEndArray();
                }
                g.writeEndObject();
            } catch (IOException ex) {
                throw new RuntimeException("Failed to generate JSON", ex);
            }
        });
    }
}

