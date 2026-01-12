package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.trees.UnaryLike;

import java.util.List;
import java.util.function.Function;

public abstract class UnaryNode extends LogicalPlan implements UnaryLike<LogicalPlan> {
    public final LogicalPlan child;

    public UnaryNode(LogicalPlan child) {
        this.child = child;
    }

    @Override
    public final List<LogicalPlan> children() {
        return List.of(child);
    }

    @Override
    public LogicalPlan child() {
        return child;
    }

    @Override
    public LogicalPlan mapChildren(Function<LogicalPlan, LogicalPlan> f) {
        return UnaryLike.mapChildren(this, f);
    }

    @Override
    protected LogicalPlan withNewChildrenInternal(List<LogicalPlan> newChildren) {
        return UnaryLike.withNewChildrenInternal(this, newChildren);
    }
}
