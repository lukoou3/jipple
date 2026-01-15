package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.Expression;

public class Filter extends UnaryNode {
    public final Expression condition;

    public Filter(Expression condition, LogicalPlan child) {
        super(child);
        this.condition = condition;
    }

    @Override
    public Object[] args() {
        return new Object[] { condition, child };
    }

    @Override
    public LogicalPlan withNewChildInternal(LogicalPlan newChild) {
        return new Filter(condition, newChild);
    }
}
