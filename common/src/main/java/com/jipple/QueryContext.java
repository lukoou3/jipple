package com.jipple;


/**
 * Query context of a {@link RippleThrowable}. It helps users understand where error occur
 * while executing queries.
 *
 * @since 3.4.0
 */
public interface QueryContext {
    // The object type of the query which throws the exception.
    // If the exception is directly from the main query, it should be an empty string.
    // Otherwise, it should be the exact object type in upper case. For example, a "VIEW".
    String objectType();

    // The object name of the query which throws the exception.
    // If the exception is directly from the main query, it should be an empty string.
    // Otherwise, it should be the object name. For example, a view name "V1".
    String objectName();

    // The starting index in the query text which throws the exception. The index starts from 0.
    int startIndex();

    // The stopping index in the query which throws the exception. The index starts from 0.
    int stopIndex();

    // The corresponding fragment of the query which throws the exception.
    String fragment();
}
