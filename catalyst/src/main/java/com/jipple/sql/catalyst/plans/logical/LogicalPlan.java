package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.plans.QueryPlan;

public abstract class LogicalPlan extends QueryPlan<LogicalPlan> {
    private Boolean _resolved;

    public boolean resolved() {
        if (_resolved == null) {
            _resolved = expressions().stream().allMatch(Expression::resolved) && childrenResolved();
        }
        return _resolved;
    }

    public boolean childrenResolved() {
        return children().stream().allMatch(LogicalPlan::resolved);
    }
}
