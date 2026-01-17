package com.jipple.sql.catalyst.analysis.rule;

import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.named.Alias;
import com.jipple.sql.catalyst.expressions.named.NamedExpression;
import com.jipple.sql.catalyst.expressions.named.UnresolvedAlias;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.plans.logical.Project;
import com.jipple.sql.catalyst.rules.Rule;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Replaces [[UnresolvedAlias]]s with concrete aliases.
 */
public class ResolveAliases extends Rule<LogicalPlan> {
    private List<Expression> assignAliases(List<Expression> exprs) {
        return exprs.stream().map(expr -> {
            if (expr instanceof UnresolvedAlias u) {
                Expression e = u.child;
                if (e instanceof NamedExpression) {
                    return e;
                } else if (!e.resolved()) {
                    return u;
                } else if (e instanceof Cast c && c.child instanceof NamedExpression ne) {
                    return new Alias(c, ne.name());
                }
                return new Alias(e, e.sql());
            } else {
                return expr;
            }
        }).collect(Collectors.toList());
    }

    private boolean hasUnresolvedAlias(List<Expression> exprs) {
        for (Expression expr : exprs) {
            if (expr instanceof UnresolvedAlias) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.transformUp(p -> {
            if (p instanceof Project project && project.child.resolved() && hasUnresolvedAlias(project.projectList)) {
                return new Project(assignAliases(project.projectList), project.child);
            } else {
                return p;
            }
        });
    }
}