package com.jipple.sql.catalyst.expressions.codegen;

/**
 * A global variable java expression.
 */
public class GlobalValue implements ExprValue {
    public final String value;
    public final Class<?> javaType;

    public GlobalValue(String value, Class<?> javaType) {
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
        GlobalValue that = (GlobalValue) obj;
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }
        return javaType != null ? javaType.equals(that.javaType) : that.javaType == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        return result;
    }
}

