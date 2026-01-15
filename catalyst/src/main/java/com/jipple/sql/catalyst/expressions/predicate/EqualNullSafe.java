package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;

public class EqualNullSafe extends BinaryComparison {
    public EqualNullSafe(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String symbol() {
        return "<=>";
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public Object eval(InternalRow input) {
        Object input1 = left.eval(input);
        Object input2 = right.eval(input);
        if (input1 == null && input2 == null) {
            return true;
        } else if (input1 == null || input2 == null) {
            return false;
        } else {
            return comparator().compare(input1, input2) == 0;
        }
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new EqualNullSafe(newLeft, newRight);
    }
}
