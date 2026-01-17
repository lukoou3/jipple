package com.jipple.sql.catalyst.analysis;

import com.jipple.sql.AnalysisException;
import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Resolver;
import com.jipple.sql.catalyst.expressions.complextype.GetStructField;
import com.jipple.sql.catalyst.expressions.named.Alias;
import com.jipple.sql.catalyst.expressions.named.NamedExpression;
import com.jipple.sql.catalyst.expressions.named.UnresolvedAlias;
import com.jipple.sql.catalyst.expressions.named.UnresolvedAttribute;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.plans.logical.Project;
import com.jipple.sql.catalyst.plans.logical.SubqueryAlias;
import com.jipple.sql.catalyst.plans.logical.UnresolvedRelation;
import com.jipple.sql.catalyst.rules.Rule;
import com.jipple.sql.catalyst.rules.RuleExecutor;
import com.jipple.sql.catalyst.trees.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
                        new ResolveRelations(),
                        new ResolveReferences(),
                        new ResolveAliases()
                )
        );
    }

    Resolver resolver() {
        return Resolver.caseInsensitiveResolution();
    }

    /** Catches any AnalysisExceptions thrown by `f` and attaches `t`'s position if any. */
    static <A> A withPosition(TreeNode<?> t, Supplier<A> f) {
        try {
            return f.get();
        } catch (AnalysisException a) {
            throw a.withPosition(t.origin());
        }
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

    /**
     * Replaces [[UnresolvedAlias]]s with concrete aliases.
     */
    class ResolveAliases extends Rule<LogicalPlan> {
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
}
