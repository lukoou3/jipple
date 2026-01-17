package com.jipple.sql.catalyst.analysis.rule;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.complextype.GetStructField;
import com.jipple.sql.catalyst.expressions.named.Alias;
import com.jipple.sql.catalyst.expressions.named.UnresolvedAttribute;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;

/**
 * Replaces [[UnresolvedAttribute]]s with concrete [[AttributeReference]]s from
 * a logical plan node's children.
 */
public class ResolveReferences extends Rule<LogicalPlan> {

    private Expression innerResolve(Expression e, LogicalPlan q, boolean trimAlias, boolean isTopLevel) {
        if (e.resolved()) {
            return e;
        }
        // case f: LambdaFunction if !f.bound => f
        else if (e instanceof UnresolvedAttribute u) {
            System.out.println("get UnresolvedAttribute:" + u);
            Expression resolved = withPosition(u, () ->
                    q.resolveChildren(u.nameParts, resolver()).getOrElse(u)
            );
            // As the comment of method `resolveExpressionTopDown`'s param `trimAlias` said,
            // when trimAlias = true, we will trim unnecessary alias of `GetStructField` and
            // we won't trim the alias of top-level `GetStructField`. Since we will call
            // CleanupAliases later in Analyzer, trim non top-level unnecessary alias of
            // `GetStructField` here is safe.
            if (resolved instanceof Alias a && a.child instanceof GetStructField s && trimAlias && !isTopLevel) {
                return s;
            } else {
                return resolved;
            }
        } else {
            return e.mapChildren(x -> innerResolve(x, q, trimAlias, false));
        }
    }

    private Expression resolveExpressionTopDown(Expression e, LogicalPlan q, boolean trimAlias) {
        return innerResolve(e, q, trimAlias,true);
    }

    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.transformUp(p -> {
            if (!p.childrenResolved()) {
                return p;
            }
            return p.mapExpressions(e -> resolveExpressionTopDown(e, p,  true));
            //return p;
        });
    }
}