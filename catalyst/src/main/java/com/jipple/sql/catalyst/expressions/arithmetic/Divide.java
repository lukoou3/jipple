package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.*;

import java.util.function.BiFunction;

import static com.jipple.sql.types.DataTypes.DOUBLE;

public class Divide extends DivModLike {
    BiFunction<Object, Object, Object> div;

    public Divide(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public AbstractDataType inputType() {
        return DOUBLE;
    }

    @Override
    public String symbol() {
        return "/";
    }

    @Override
    protected String decimalMethod() {
        return "div";
    }

    @Override
    public Object evalOperation(Object left, Object right) {
        if (div == null) {
            DataType dataType = dataType();
            if (dataType instanceof DoubleType) {
                div = (x, y) -> (Double) x / (Double) y;
            } else {
                throw new RuntimeException();
            }
        }
        return div.apply(left, right);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Divide(newLeft, newRight);
    }
}
