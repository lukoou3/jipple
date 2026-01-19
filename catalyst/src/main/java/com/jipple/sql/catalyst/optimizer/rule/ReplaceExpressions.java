package com.jipple.sql.catalyst.optimizer.rule;

import com.jipple.sql.catalyst.expressions.RuntimeReplaceable;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;

/**
 * Finds all the [[RuntimeReplaceable]] expressions that are unevaluable and replace them
 * with semantically equivalent expressions that can be evaluated.
 *
 * This is mainly used to provide compatibility with other databases.
 * Few examples are:
 *   we use this to support "left" by replacing it with "substring".
 *   we use this to replace Every and Any with Min and Max respectively.
 */
public class ReplaceExpressions extends Rule<LogicalPlan> {
    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.transformAllExpressions(e -> {
            if (e instanceof RuntimeReplaceable r) {
                return r.replacement();
            } else {
                return e;
            }
        });
    }
}
