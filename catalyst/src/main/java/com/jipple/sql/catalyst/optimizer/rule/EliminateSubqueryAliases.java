package com.jipple.sql.catalyst.optimizer.rule;

import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.plans.logical.SubqueryAlias;
import com.jipple.sql.catalyst.rules.Rule;

/**
 * Removes [[SubqueryAlias]] operators from the plan. Subqueries are only required to provide
 * scoping information for attributes and can be removed once analysis is complete.
 */
public class EliminateSubqueryAliases extends Rule<LogicalPlan> {
    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.transformUp(p -> {
            if (p instanceof SubqueryAlias s) {
                return s.child;
            } else {
                return p;
            }
        });
    }
}
