package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.expressions.named.NamedExpression;

import java.util.List;

public class Expr extends UnaryNode {
    public final Expression expression;
    public Expr(Expression expression, LogicalPlan child) {
        super(child);
        if (!(expression instanceof NamedExpression)) {
            throw new IllegalArgumentException("expr must be NamedExpression");
        }
        this.expression = expression;
    }

    @Override
    public Object[] args() {
        return new Object[] {expression, child} ;
    }

    @Override
    public List<Attribute> output() {
        return List.of(((NamedExpression)expression).toAttribute());
    }

    @Override
    public LogicalPlan withNewChildInternal(LogicalPlan newChild) {
        return new Expr(expression, newChild);
    }
}
