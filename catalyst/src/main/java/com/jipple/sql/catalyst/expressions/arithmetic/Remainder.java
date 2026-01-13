package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.*;

import java.util.function.BiFunction;

import static com.jipple.sql.types.DataTypes.NUMERIC;

public class Remainder extends DivModLike {
    BiFunction<Object, Object, Object> mod;

    public Remainder(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public AbstractDataType inputType() {
        return NUMERIC;
    }

    @Override
    public String symbol() {
        return "%";
    }

    @Override
    public Object evalOperation(Object left, Object right) {
        if (mod == null) {
            DataType dataType = dataType();
            if (dataType instanceof IntegerType) {
                mod = (x, y) -> (Integer) x % (Integer) y;
            } else if (dataType instanceof LongType) {
                mod = (x, y) -> (Long) x % (Long) y;
            } else if (dataType instanceof DoubleType) {
                mod = (x, y) -> (Double) x % (Double) y;
            } else {
                throw new RuntimeException();
            }
        }
        return mod.apply(left, right);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Remainder(newLeft, newRight);
    }
}
