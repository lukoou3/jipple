package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.sql.catalyst.expressions.Expression;

public class LessThanOrEqual extends BinaryComparison {
    public LessThanOrEqual(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String symbol() {
        return "<=";
    }

    @Override
    protected Object nullSafeEval(Object input1, Object input2) {
        return comparator().compare(input1, input2) <= 0;
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new LessThanOrEqual(newLeft, newRight);
    }
}
