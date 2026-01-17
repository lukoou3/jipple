package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.named.Attribute;

import java.util.List;

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
    public List<Attribute> output() {
        return child.output();
    }

    @Override
    public LogicalPlan withNewChildInternal(LogicalPlan newChild) {
        return new Filter(condition, newChild);
    }

}
