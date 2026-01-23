package com.jipple.sql.catalyst.expressions;

/**
 * A wrapper in place of using List<Expression> to record a group of equivalent expressions.
 *
 * This saves a lot of memory when there are a lot of expressions in a same equivalence group.
 * Instead of appending to a mutable list/buffer of Expressions, just update the "flattened"
 * useCount in this wrapper in-place.
 */
public class ExpressionStats {
    public final Expression expr;
    public int useCount;

    public ExpressionStats(Expression expr, int useCount) {
        this.expr = expr;
        this.useCount = useCount;
    }
}

