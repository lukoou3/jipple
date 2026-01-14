package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.BinaryOperator;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.arithmetic.Add;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class And extends BinaryOperator {
    public And(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public AbstractDataType inputType() {
        return BOOLEAN;
    }

    @Override
    public String symbol() {
        return "&&";
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    // +---------+---------+---------+---------+
    // | AND     | TRUE    | FALSE   | UNKNOWN |
    // +---------+---------+---------+---------+
    // | TRUE    | TRUE    | FALSE   | UNKNOWN |
    // | FALSE   | FALSE   | FALSE   | FALSE   |
    // | UNKNOWN | UNKNOWN | FALSE   | UNKNOWN |
    // +---------+---------+---------+---------+

    @Override
    public Object eval(InternalRow input) {
        Object input1 = left.eval(input);
        if (Boolean.FALSE.equals(input1)) {
            return false;
        } else {
            Object input2 = right.eval(input);
            if (Boolean.FALSE.equals(input2)) {
                return false;
            } else {
                if (input1 != null && input2 != null) {
                    return true;
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Add(newLeft, newRight);
    }
}
