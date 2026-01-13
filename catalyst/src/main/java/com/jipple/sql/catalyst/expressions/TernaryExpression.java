package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;

import java.util.List;
import java.util.function.Function;

public abstract class TernaryExpression extends Expression implements TernaryLike<Expression> {
    public final Expression first;
    public final Expression second;
    public final Expression third;

    protected TernaryExpression(Expression first, Expression second, Expression third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public Object[] args() {
        return new Object[] { first, second, third };
    }

    @Override
    public final List<Expression> children() {
        return List.of(first, second, third);
    }

    @Override
    public Expression first() {
        return first;
    }

    @Override
    public Expression second() {
        return second;
    }

    @Override
    public Expression third() {
        return third;
    }

    @Override
    public boolean foldable() {
        return children().stream().allMatch(x -> x.foldable());
    }

    @Override
    public boolean nullable() {
        return children().stream().anyMatch(x -> x.nullable());
    }

    /**
     * Default behavior of evaluation according to the default nullability of TernaryExpression.
     * If subclass of TernaryExpression override nullable, probably should also override this.
     */
    @Override
    public Object eval(InternalRow input) {
        var value1 = first.eval(input);
        if (value1 != null) {
            var value2 = second.eval(input);
            if (value2 != null) {
                var value3 = third.eval(input);
                if (value3 != null) {
                    return nullSafeEval(value1, value2, value3);
                }
            }
        }
        return null;
    }

    /**
     * Called by default [[eval]] implementation.  If subclass of TernaryExpression keep the default
     * nullability, they can override this method to save null-check code.  If we need full control
     * of evaluation process, we should override [[eval]].
     */
    protected Object nullSafeEval(Object input1, Object input2, Object input3) {
        throw new UnsupportedOperationException("not implements nullSafeEval for: " + this.getClass());
    }

    @Override
    public Expression mapChildren(Function<Expression, Expression> f) {
        return TernaryLike.mapChildren(this, f);
    }

    @Override
    protected final Expression withNewChildrenInternal(List<Expression> newChildren) {
        return TernaryLike.withNewChildrenInternal(this, newChildren);
    }

}
