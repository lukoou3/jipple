package com.jipple.sql.catalyst.expressions;

import java.util.List;
import java.util.function.Function;

public abstract class LeafExpression extends Expression {
    @Override
    public final List<Expression> children() {
        return List.of();
    }

    @Override
    public final Expression mapChildren(Function<Expression, Expression> f) {
        return this;
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return this;
    }
}
