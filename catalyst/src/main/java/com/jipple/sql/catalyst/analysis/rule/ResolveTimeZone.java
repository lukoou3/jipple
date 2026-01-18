package com.jipple.sql.catalyst.analysis.rule;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.TimeZoneAwareExpression;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;

/**
 * Replace [[TimeZoneAwareExpression]] without timezone id by its copy with session local
 * time zone.
 */
public class ResolveTimeZone extends Rule<LogicalPlan> {
    private Expression transformTimeZoneExprs(Expression e) {
        if (e instanceof TimeZoneAwareExpression t && t.timeZoneId().isEmpty()) {
            return t.withTimeZone(conf().sessionLocalTimeZone());
        }
        return e;
    }

    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.resolveExpressionsUp(this::transformTimeZoneExprs);
    }

    public Expression resolveTimeZones(Expression e) {
        return e.transformUp(this::transformTimeZoneExprs);
    }
}
