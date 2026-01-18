package com.jipple.sql.catalyst.analysis;

import com.jipple.sql.catalyst.QueryPlanningTracker;
import com.jipple.sql.catalyst.analysis.rule.*;
import com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.RuleExecutor;

import java.util.List;
import java.util.Map;

public class Analyzer extends RuleExecutor<LogicalPlan> {
    private final Map<String, LogicalPlan> tempViews;
    private final SimpleFunctionRegistry functionRegistry;

    public Analyzer(Map<String, LogicalPlan> tempViews) {
        this(tempViews, FunctionRegistry.builtin.clone());
    }

    public Analyzer(Map<String, LogicalPlan> tempViews, SimpleFunctionRegistry functionRegistry) {
        this.tempViews = tempViews;
        this.functionRegistry = functionRegistry;
    }

    public LogicalPlan executeAndCheck(LogicalPlan plan, QueryPlanningTracker tracker) {
        if (plan.analyzed()) {
            return plan;
        }
        LogicalPlan analyzed = executeAndTrack(plan, tracker);
        CheckAnalysis.checkAnalysis(analyzed);
        return analyzed;
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
                        new ResolveRelations(tempViews),
                        new ResolveReferences(),
                        new ResolveAliases(),
                        new ResolveFunctions(functionRegistry),
                        new ResolveTimeZone(),
                        TypeCoercion.typeCoercionRules()
                )
        );
    }

    /*Resolver resolver() {
        return Resolver.caseInsensitiveResolution();
    }


    static <A> A withPosition(TreeNode<?> t, Supplier<A> f) {
        try {
            return f.get();
        } catch (AnalysisException a) {
            throw a.withPosition(t.origin());
        }
    }*/
}
