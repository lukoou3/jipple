package com.jipple.sql.execution;

import com.jipple.error.JippleException;
import com.jipple.sql.catalyst.QueryPlanningTracker;
import com.jipple.sql.catalyst.analysis.Analyzer;
import com.jipple.sql.catalyst.analysis.SimpleFunctionRegistry;
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

    public QueryExecution(Map<String, LogicalPlan> tempViews, SimpleFunctionRegistry functionRegistry, LogicalPlan logical) {
        this(tempViews, functionRegistry, logical, new QueryPlanningTracker());
    }

    public QueryExecution(Map<String, LogicalPlan> tempViews, SimpleFunctionRegistry functionRegistry, LogicalPlan logical, QueryPlanningTracker tracker) {
        this.tempViews = tempViews;
        this.functionRegistry = functionRegistry;
        this.logical = logical;
        this.tracker = tracker;
        this.analyzer = new Analyzer(tempViews, functionRegistry);
    }

    private LogicalPlan _analyzed;

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
