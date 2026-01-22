package com.jipple.sql.catalyst.expressions.codegen;

/**
 * A local variable java expression.
 */
public class VariableValue implements ExprValue {
    public final String variableName;
    public final Class<?> javaType;

    public VariableValue(String variableName, Class<?> javaType) {
        this.variableName = variableName;
        this.javaType = javaType;
    }

    @Override
    public String code() {
        return variableName;
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
        VariableValue that = (VariableValue) obj;
        if (variableName != null ? !variableName.equals(that.variableName) : that.variableName != null) {
            return false;
        }
        return javaType != null ? javaType.equals(that.javaType) : that.javaType == null;
    }

    @Override
    public int hashCode() {
        int result = variableName != null ? variableName.hashCode() : 0;
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        return result;
    }
}

