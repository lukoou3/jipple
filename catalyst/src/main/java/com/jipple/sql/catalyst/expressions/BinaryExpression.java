package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.trees.BinaryLike;

import java.util.List;
import java.util.function.Function;

public abstract class BinaryExpression extends Expression implements BinaryLike<Expression> {
    public final Expression left;
    public final Expression right;

    public BinaryExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Object[] args() {
        return new Object[] { left, right };
    }

    @Override
    public final List<Expression> children() {
        return List.of(left, right);
    }

    @Override
    public Expression left() {
        return left;
    }

    @Override
    public Expression right() {
        return right;
    }

    @Override
    public boolean foldable() {
        return left.foldable() && right.foldable();
    }

    @Override
    public boolean nullable() {
        return left.nullable() || right.nullable();
    }

    /**
     * Default behavior of evaluation according to the default nullability of BinaryExpression.
     * If subclass of BinaryExpression override nullable, probably should also override this.
     */
    @Override
    public Object eval(InternalRow input) {
        var value1 = left.eval(input);
        if (value1 == null) {
            return null;
        } else {
            var value2 = right.eval(input);
            if (value2 == null) {
                return null;
            } else {
                return nullSafeEval(value1, value2);
            }
        }
    }

    /**
     * Called by default [[eval]] implementation.  If subclass of BinaryExpression keep the default
     * nullability, they can override this method to save null-check code.  If we need full control
     * of evaluation process, we should override [[eval]].
     */
    protected Object nullSafeEval(Object input1, Object input2) {
        throw new UnsupportedOperationException("not implements nullSafeEval for: " + this.getClass());
    }

    @Override
    public Expression mapChildren(Function<Expression, Expression> f) {
        return BinaryLike.mapChildren(this, f);
    }

    @Override
    protected final Expression withNewChildrenInternal(List<Expression> newChildren) {
        return BinaryLike.withNewChildrenInternal(this, newChildren);
    }

}
