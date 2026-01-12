package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.trees.UnaryLike;

import java.util.List;
import java.util.function.Function;

/**
 * An expression with one input and one output. The output is by default evaluated to null
 * if the input is evaluated to null.
 */
public abstract class UnaryExpression extends Expression implements UnaryLike<Expression> {
    public final Expression child;

    public UnaryExpression(Expression child) {
        this.child = child;
    }

    @Override
    public Object[] args() {
        return new Object[] { child };
    }

    @Override
    public final List<Expression> children() {
        return List.of(child);
    }

    @Override
    public Expression child() {
        return child;
    }

    @Override
    public boolean foldable() {
        return child.foldable();
    }

    @Override
    public boolean nullable() {
        return child.nullable();
    }

    @Override
    public Expression mapChildren(Function<Expression, Expression> f) {
        return UnaryLike.mapChildren(this, f);
    }

    @Override
    protected final Expression withNewChildrenInternal(List<Expression> newChildren) {
        return UnaryLike.withNewChildrenInternal(this, newChildren);
    }
}
