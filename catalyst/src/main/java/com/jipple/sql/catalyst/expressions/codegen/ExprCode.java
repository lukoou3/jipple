package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.types.DataType;

import java.io.Serializable;

public class ExprCode implements Serializable {
    public Block code;
    public ExprValue isNull;
    public ExprValue value;

    /**
     * Java source for evaluating an {@link com.jipple.sql.catalyst.expressions.Expression} given a
     * {@link com.jipple.sql.catalyst.InternalRow} of input.
     *
     * @param code The sequence of statements required to evaluate the expression.
     *             It should be empty string, if {@code isNull} and {@code value} are already existed,
     *             or no code needed to evaluate them (literals).
     * @param isNull A term that holds a boolean value representing whether the expression evaluated
     *               to null.
     * @param value A term for a (possibly primitive) value of the result of the evaluation. Not
     *              valid if {@code isNull} is set to {@code true}.
     */
    public ExprCode(Block code, ExprValue isNull, ExprValue value) {
        this.code = code;
        this.isNull = isNull;
        this.value = value;
    }

    public static ExprCode apply(ExprValue isNull, ExprValue value) {
        return new ExprCode(EmptyBlock.INSTANCE, isNull, value);
    }

    public static ExprCode forNullValue(DataType dataType) {
        return new ExprCode(EmptyBlock.INSTANCE, TrueLiteral.INSTANCE, JavaCode.defaultLiteral(dataType));
    }

    public static ExprCode forNonNullValue(ExprValue value) {
        return new ExprCode(EmptyBlock.INSTANCE, FalseLiteral.INSTANCE, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ExprCode that = (ExprCode) obj;
        if (code != null ? !code.equals(that.code) : that.code != null) {
            return false;
        }
        if (isNull != null ? !isNull.equals(that.isNull) : that.isNull != null) {
            return false;
        }
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (isNull != null ? isNull.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExprCode(code=" + code + ", isNull=" + isNull + ", value=" + value + ")";
    }
}
