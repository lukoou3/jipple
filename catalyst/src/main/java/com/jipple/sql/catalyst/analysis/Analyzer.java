package com.jipple.sql.catalyst.analysis;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.named.UnresolvedAttribute;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.plans.logical.SubqueryAlias;
import com.jipple.sql.catalyst.plans.logical.UnresolvedRelation;
import com.jipple.sql.catalyst.rules.Rule;
import com.jipple.sql.catalyst.rules.RuleExecutor;

import java.util.List;
import java.util.Map;

public class Analyzer extends RuleExecutor<LogicalPlan> {
    private final Map<String, LogicalPlan> tempViews;

    public Analyzer(Map<String, LogicalPlan> tempViews) {
        this.tempViews = tempViews;
    }


    /**
     * If the plan cannot be resolved within maxIterations, analyzer will throw exception to inform
     * user to increase the value of SQLConf.ANALYZER_MAX_ITERATIONS.
     */
    protected FixedPoint fixedPoint(){
        // TODO: get conf
        return new FixedPoint(
                100,
                true,
                "jipple.sql.analyzer.maxIterations"
        );
    }

    @Override
    protected List<Batch> batches() {
        return List.of(
                new Batch("Resolution", fixedPoint(),
                        new ResolveRelations()
                )
        );
    }

    /**
     * Replaces [[UnresolvedRelation]]s with concrete relations from the catalog.
     */
    class ResolveRelations extends Rule<LogicalPlan> {
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

    /**
     * Replaces [[UnresolvedAttribute]]s with concrete [[AttributeReference]]s from
     * a logical plan node's children.
     */
    class ResolveReferences extends Rule<LogicalPlan> {

        private Expression innerResolve(Expression e, LogicalPlan q, boolean isTopLevel) {
            if (e.resolved()) {
                return e;
            }
            if (e instanceof UnresolvedAttribute u) {
                System.out.println("get UnresolvedAttribute:" + u);
                return e;
            } else {
                return e.mapChildren(x -> innerResolve(x, q, false));
            }
        }

       private Expression resolveExpressionTopDown(Expression e, LogicalPlan q, boolean trimAlias) {
           return innerResolve(e, q, true);
       }

        @Override
        public LogicalPlan apply(LogicalPlan plan) {
            return plan.transformUp(p -> {
                if (!p.childrenResolved()) {
                    return p;
                }
                //p.mapExpressions(resolveExpressionTopDown(_, q))
                return p;
            });
        }
    }


}
