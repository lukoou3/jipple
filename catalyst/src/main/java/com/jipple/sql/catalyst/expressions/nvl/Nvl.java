package com.jipple.sql.catalyst.expressions.nvl;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.InheritAnalysisRules;

import java.util.List;

public class Nvl extends InheritAnalysisRules {
    public final Expression left;
    public final Expression right;
    public final Expression replacement;

    public Nvl(Expression left, Expression right, Expression replacement) {
        this.left = left;
        this.right = right;
        this.replacement = replacement;
    }

    public Nvl(Expression left, Expression right) {
        this(left, right, new Coalesce(List.of(left, right)));
    }

    @Override
    public Object[] args() {
        return new Object[] { left, right, replacement };
    }

    @Override
    public Expression replacement() {
        return replacement;
    }

    @Override
    public List<Expression> parameters() {
        return List.of(left, right);
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new Nvl(left, right, newChild);
    }
}
