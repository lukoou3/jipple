package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.*;

import java.util.function.BiFunction;

import static com.jipple.sql.types.DataTypes.NUMERIC;

public class Multiply extends BinaryArithmetic {
    BiFunction<Object, Object, Object> multiply;

    public Multiply(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public AbstractDataType inputType() {
        return NUMERIC;
    }

    @Override
    public String symbol() {
        return "*";
    }

    @Override
    protected String decimalMethod() {
        return "times";
    }

    @Override
    protected Object nullSafeEval(Object input1, Object input2) {
        if (multiply == null) {
            DataType dataType = dataType();
            if (dataType instanceof IntegerType) {
                multiply = (x, y) -> (Integer) x * (Integer) y;
            } else if (dataType instanceof LongType) {
                multiply = (x, y) -> (Long) x * (Long) y;
            } else if (dataType instanceof DoubleType) {
                multiply = (x, y) -> (Double) x * (Double) y;
            } else {
                throw new RuntimeException();
            }
        }
        return multiply.apply(input1, input2);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Multiply(newLeft, newRight);
    }
}
