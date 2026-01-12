package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.*;

import java.util.function.BiFunction;

import static com.jipple.sql.types.DataTypes.NUMERIC;

public class Subtract extends BinaryArithmetic {
    BiFunction<Object, Object, Object> minus;

    public Subtract(Expression left, Expression right) {
        super(left, right);
    }


    @Override
    public AbstractDataType inputType() {
        return NUMERIC;
    }

    @Override
    public String symbol() {
        return "-";
    }

    @Override
    protected Object nullSafeEval(Object input1, Object input2) {
        if (minus == null) {
            DataType dataType = dataType();
            if (dataType instanceof IntegerType) {
                minus = (x, y) -> (Integer) x - (Integer) y;
            } else if (dataType instanceof LongType) {
                minus = (x, y) -> (Long) x - (Long) y;
            } else if (dataType instanceof DoubleType) {
                minus = (x, y) -> (Double) x - (Double) y;
            } else {
                throw new RuntimeException();
            }
        }
        return minus.apply(input1, input2);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Subtract(newLeft, newRight);
    }
}
