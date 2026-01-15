package com.jipple.sql.catalyst.plans.logical;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class LeafNode extends LogicalPlan {
    @Override
    public List<LogicalPlan> children() {
        return Collections.emptyList();
    }

    @Override
    public LogicalPlan mapChildren(Function<LogicalPlan, LogicalPlan> f) {
        return this;
    }

    @Override
    protected LogicalPlan withNewChildrenInternal(List<LogicalPlan> newChildren) {
        return this;
    }

}
