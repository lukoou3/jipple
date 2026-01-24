package com.jipple.sql.catalyst.expressions.codegen;

/**
 * A typed java fragment that must be a valid java expression.
 */
public abstract class ExprValue implements JavaCode {
    public abstract Class<?> javaType();

    @Override
    public String toString() {
        return code();
    }

    public boolean isPrimitive() {
        return javaType().isPrimitive();
    }

    /**
     * Converts an ExprValue to a String (its code).
     */
    public static String exprValueToString(ExprValue exprValue) {
        return exprValue.code();
    }
}

