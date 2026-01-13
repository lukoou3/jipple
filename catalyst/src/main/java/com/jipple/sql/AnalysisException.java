package com.jipple.sql;

/**
 * Thrown when a query fails to analyze, usually because the query itself is invalid.
 *
 */
public class AnalysisException extends RuntimeException {
    private final String message;
    private final Integer line;
    private final Integer startPosition;
    private final Throwable cause;

    public AnalysisException(String message) {
        this(message, null, null, null);
    }

    public AnalysisException(String message, Integer line, Integer startPosition) {
        this(message, line, startPosition, null);
    }

    public AnalysisException(String message, Integer line, Integer startPosition, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.line = line;
        this.startPosition = startPosition;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        if (line != null || startPosition != null) {
            String lineAnnotation = line != null ? " line " + line : "";
            String positionAnnotation = startPosition != null ? " pos " + startPosition : "";
            return message + lineAnnotation + positionAnnotation;
        }
        return message;
    }

}
