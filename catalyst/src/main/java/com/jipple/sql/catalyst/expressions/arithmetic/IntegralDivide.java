package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.*;

import java.util.function.BiFunction;

import static com.jipple.sql.types.DataTypes.LONG;

public class IntegralDivide extends DivModLike {
    BiFunction<Object, Object, Object> div;
    public IntegralDivide(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public AbstractDataType inputType() {
        return LONG;
    }

    @Override
    public String symbol() {
        return "/";
    }

    @Override
    protected String decimalMethod() {
        return "quot";
    }

    @Override
    public String sqlOperator() {
        return "div";
    }

    @Override
    public Object evalOperation(Object left, Object right) {
        if (div == null) {
            DataType dataType = dataType();
            if (dataType instanceof LongType) {
                div = (x, y) -> (Long) x / (Long) y;
            } else {
                throw new RuntimeException();
            }
        }
        return div.apply(left, right);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new IntegralDivide(newLeft, newRight);
    }
}
