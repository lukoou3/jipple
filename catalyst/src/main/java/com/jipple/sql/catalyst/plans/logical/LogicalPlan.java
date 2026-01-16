package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.plans.QueryPlan;

import java.util.function.BiFunction;
import java.util.function.Supplier;

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

    @Override
    protected String statePrefix() {
        return !resolved() ? "'" : super.statePrefix();
    }

    /**
     * Create a plan using the block of code when the given context exists. Otherwise return the
     * original plan.
     */
    public LogicalPlan optional(Object ctx, Supplier<LogicalPlan> f) {
        return ctx != null ? f.get() : this;
    }


    /**
     * Map a [[LogicalPlan]] to another [[LogicalPlan]] if the passed context exists using the
     * passed function. The original plan is returned when the context does not exist.
     */
    public <C> LogicalPlan optionalMap(C ctx, BiFunction<C, LogicalPlan, LogicalPlan> f) {
        return ctx != null ? f.apply(ctx, this) : this;
    }

}
