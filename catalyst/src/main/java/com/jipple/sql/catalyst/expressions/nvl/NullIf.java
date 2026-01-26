package com.jipple.sql.catalyst.expressions.nvl;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.InheritAnalysisRules;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.condition.If;
import com.jipple.sql.catalyst.expressions.predicate.EqualTo;

import java.util.List;

public class NullIf extends InheritAnalysisRules {
    public final Expression left;
    public final Expression right;
    public final Expression replacement;

    public NullIf(Expression left, Expression right, Expression replacement) {
        this.left = left;
        this.right = right;
        this.replacement = replacement;
    }

    public NullIf(Expression left, Expression right) {
        this(left, right, new If(new EqualTo(left, right), Literal.create(null, left.dataType()), left));
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
        return new NullIf(left, right, newChild);
    }
}
