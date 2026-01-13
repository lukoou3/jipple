package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;

// Common base trait for Divide and Remainder, since these two classes are almost identical
public abstract class DivModLike extends BinaryArithmetic  {
    public DivModLike(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public boolean nullable() {
        return true;
    }

    @Override
    public final Object eval(InternalRow input) {
        // evaluate right first as we have a chance to skip left if right is 0
        Object input2 = right.eval(input);
        if (input2 == null || ((Number)input2).intValue() == 0) {
            return null;
        }  else {
            Object input1 = left.eval(input);
            if (input1 == null) {
                return null;
            } else {
                return evalOperation(input1, input2);
            }
        }
    }

    public abstract Object evalOperation(Object left, Object right);
}
