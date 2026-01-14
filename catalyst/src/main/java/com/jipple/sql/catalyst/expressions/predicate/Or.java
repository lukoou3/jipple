package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.BinaryOperator;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class Or extends BinaryOperator {
    public Or(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public AbstractDataType inputType() {
        return BOOLEAN;
    }

    @Override
    public String symbol() {
        return "||";
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    // +---------+---------+---------+---------+
    // | OR      | TRUE    | FALSE   | UNKNOWN |
    // +---------+---------+---------+---------+
    // | TRUE    | TRUE    | TRUE    | TRUE    |
    // | FALSE   | TRUE    | FALSE   | UNKNOWN |
    // | UNKNOWN | TRUE    | UNKNOWN | UNKNOWN |
    // +---------+---------+---------+---------+
    @Override
    public Object eval(InternalRow input) {
        Object input1 = left.eval(input);
        if (Boolean.TRUE.equals(input1)) {
            return true;
        } else {
            Object input2 = right.eval(input);
            if (Boolean.TRUE.equals(input2)) {
                return true;
            } else {
                if (input1 != null && input2 != null) {
                    return false;
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Or(newLeft, newRight);
    }
}
