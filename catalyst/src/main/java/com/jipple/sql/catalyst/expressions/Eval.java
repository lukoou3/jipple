package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;

public abstract class Eval extends ExpressionsEvaluator {
    public abstract Object eval(InternalRow r);
}
