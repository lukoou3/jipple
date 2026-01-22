package com.jipple.sql.catalyst.expressions.codegen;

import java.io.Serializable;

/**
 * A literal java expression.
 */
public class LiteralValue implements ExprValue, Serializable {
    private final String value;
    private final Class<?> javaType;

    public LiteralValue(String value, Class<?> javaType) {
        this.value = value;
        this.javaType = javaType;
    }

    @Override
    public String code() {
        return value;
    }

    @Override
    public Class<?> javaType() {
        return javaType;
    }

    @Override
    public String toString() {
        return code();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LiteralValue that = (LiteralValue) obj;
        return javaType.equals(that.javaType) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode() * 31 + javaType.hashCode();
    }
}

