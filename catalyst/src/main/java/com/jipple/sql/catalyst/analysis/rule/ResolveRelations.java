package com.jipple.sql.catalyst.analysis.rule;

import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.plans.logical.SubqueryAlias;
import com.jipple.sql.catalyst.plans.logical.UnresolvedRelation;
import com.jipple.sql.catalyst.rules.Rule;

import java.util.Map;

/**
 * Replaces [[UnresolvedRelation]]s with concrete relations from the catalog.
 */
public class ResolveRelations extends Rule<LogicalPlan>  {
    private final Map<String, LogicalPlan> tempViews;

    public ResolveRelations(Map<String, LogicalPlan> tempViews) {
        this.tempViews = tempViews;
    }

    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.transformUp(p -> {
            if (p instanceof UnresolvedRelation u && u.multipartIdentifier.size() == 1) {
                String ident = u.multipartIdentifier.get(0);
                LogicalPlan table = tempViews.get(ident);
                return table != null? new SubqueryAlias(ident, table): p;
            }
            return p;
        });
    }
}
