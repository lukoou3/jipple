package com.jipple.sql.catalyst.expressions.named;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.unresolved.UnresolvedException;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.types.DataType;

public class UnresolvedAlias extends UnaryExpression implements NamedExpression {
    public UnresolvedAlias(Expression child) {
        super(child);
    }

    /** Unevaluable is not foldable because we don't have an eval for it. */
    @Override
    public boolean foldable() {
        return false;
    }

    @Override
    public boolean nullable() {
        throw new UnresolvedException("dataType");
    }

    @Override
    public DataType dataType() {
        throw new UnresolvedException("dataType");
    }

    @Override
    public Object eval(InternalRow input) {
        throw new UnsupportedOperationException("Cannot evaluate expression: " + this);
    }

    @Override
    public String name() {
        throw new UnresolvedException("name");
    }

    @Override
    public Object[] args() {
        return new Object[] { child };
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new UnresolvedAlias(newChild);
    }
}
