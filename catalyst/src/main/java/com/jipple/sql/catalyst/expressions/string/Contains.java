package com.jipple.sql.catalyst.expressions.string;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.unsafe.types.UTF8String;

public class Contains extends StringPredicate {
    public Contains(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public boolean compare(UTF8String l, UTF8String r) {
        return l.contains(r);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Contains(newLeft, newRight);
    }
}
