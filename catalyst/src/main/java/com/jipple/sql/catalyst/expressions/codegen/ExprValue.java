package com.jipple.sql.catalyst.expressions.codegen;

/**
 * A typed java fragment that must be a valid java expression.
 */
public interface ExprValue extends JavaCode {
    Class<?> javaType();

    default boolean isPrimitive() {
        return javaType().isPrimitive();
    }

    /**
     * Converts an ExprValue to a String (its code).
     */
    static String exprValueToString(ExprValue exprValue) {
        return exprValue.code();
    }
}

