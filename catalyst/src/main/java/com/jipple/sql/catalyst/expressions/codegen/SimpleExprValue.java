package com.jipple.sql.catalyst.expressions.codegen;

/**
 * A java expression fragment.
 */
public class SimpleExprValue implements ExprValue {
    private final String expr;
    private final Class<?> javaType;

    public SimpleExprValue(String expr, Class<?> javaType) {
        this.expr = expr;
        this.javaType = javaType;
    }

    @Override
    public String code() {
        return "(" + expr + ")";
    }

    @Override
    public Class<?> javaType() {
        return javaType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SimpleExprValue that = (SimpleExprValue) obj;
        if (expr != null ? !expr.equals(that.expr) : that.expr != null) {
            return false;
        }
        return javaType != null ? javaType.equals(that.javaType) : that.javaType == null;
    }

    @Override
    public int hashCode() {
        int result = expr != null ? expr.hashCode() : 0;
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        return result;
    }
}

