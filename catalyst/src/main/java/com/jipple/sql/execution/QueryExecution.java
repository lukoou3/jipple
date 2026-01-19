package com.jipple.sql.execution;

import com.jipple.error.JippleException;
import com.jipple.sql.catalyst.QueryPlanningTracker;
import com.jipple.sql.catalyst.analysis.Analyzer;
import com.jipple.sql.catalyst.analysis.SimpleFunctionRegistry;
import com.jipple.sql.catalyst.optimizer.Optimizer;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;

import java.util.Map;
import java.util.function.Supplier;

/**
 * The primary workflow for executing relational queries using Ripple.  Designed to allow easy
 * access to the intermediate phases of query execution for developers.
 *
 * While this is not a public class, we should avoid changing the function names for the sake of
 * changing them, because a lot of developers use the feature for debugging.
 */
public class QueryExecution {
    public final Map<String, LogicalPlan> tempViews;
    public final SimpleFunctionRegistry functionRegistry;
    public final LogicalPlan logical;
    public final QueryPlanningTracker tracker;
    public final Analyzer analyzer;
    public final Optimizer optimizer;

    public QueryExecution(Map<String, LogicalPlan> tempViews, SimpleFunctionRegistry functionRegistry, LogicalPlan logical) {
        this(tempViews, functionRegistry, logical, new QueryPlanningTracker());
    }

    public QueryExecution(Map<String, LogicalPlan> tempViews, SimpleFunctionRegistry functionRegistry, LogicalPlan logical, QueryPlanningTracker tracker) {
        this.tempViews = tempViews;
        this.functionRegistry = functionRegistry;
        this.logical = logical;
        this.tracker = tracker;
        this.analyzer = new Analyzer(tempViews, functionRegistry);
        this.optimizer = new Optimizer();
    }

    private LogicalPlan _analyzed;
    private LogicalPlan _optimizedPlan;

    public void resetAnalyzed() {
        analyzed();
    }

    // analyzer 阶段
    public LogicalPlan analyzed() {
        if (_analyzed == null) {
            LogicalPlan plan = executePhase(QueryPlanningTracker.ANALYSIS, () ->
                analyzer.executeAndCheck(logical, tracker)
            );
            tracker.setAnalyzed(plan);
            return plan;
        }
        return _analyzed;
    }

    // optimizer 阶段
    public LogicalPlan optimizedPlan() {
        if (_optimizedPlan == null) {
            LogicalPlan plan = executePhase(QueryPlanningTracker.OPTIMIZATION, () ->
                    optimizer.executeAndTrack(analyzed(), tracker)
            );
            plan.setAnalyzed();
            return plan;
        }
        return _optimizedPlan;
    }

    public void assertOptimized() {
        optimizedPlan();
    }

    protected <T> T executePhase(String phase, Supplier<T> block) {
        try {
            return tracker.measurePhase(phase, block);
        } catch (NullPointerException | AssertionError e) {
            throw JippleException.internalError(
                    "The Ripple SQL phase " + phase + " failed with an internal error." +
                            " You hit a bug in Ripple or the Ripple plugins you use. Please, report this bug " +
                            "to the corresponding communities or vendors, and provide the full stack trace.",
                    e);
        }
    }
}
