package com.jipple.sql.catalyst.plans.logical;

public class Filter extends UnaryNode {
    public final Exception condition;

    public Filter(Exception condition, LogicalPlan child) {
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
