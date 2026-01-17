package com.jipple.sql.catalyst;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.util.BoundedPriorityQueue;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A simple utility for tracking runtime and associated stats in query planning.
 *
 * There are two separate concepts we track:
 *
 * 1. Phases: These are broad scope phases in query planning, as listed below, i.e. analysis,
 * optimization and physical planning (just planning).
 *
 * 2. Rules: These are the individual Catalyst rules that we track. In addition to time, we also
 * track the number of invocations and effective invocations.
 */
public class QueryPlanningTracker {

    // Define a list of common phases here.
    public static final String PARSING = "parsing";
    public static final String ANALYSIS = "analysis";
    public static final String OPTIMIZATION = "optimization";
    public static final String PLANNING = "planning";

    public static class RuleSummary {
        public long totalTimeNs;
        public long numInvocations;
        public long numEffectiveInvocations;

        public RuleSummary() {
            this(0, 0, 0);
        }

        /**
         * Summary for a rule.
         * @param totalTimeNs total amount of time, in nanosecs, spent in this rule.
         * @param numInvocations number of times the rule has been invoked.
         * @param numEffectiveInvocations number of times the rule has been invoked and
         *                                resulted in a plan change.
         */
        public RuleSummary(long totalTimeNs, long numInvocations, long numEffectiveInvocations) {
            this.totalTimeNs = totalTimeNs;
            this.numInvocations = numInvocations;
            this.numEffectiveInvocations = numEffectiveInvocations;
        }

        @Override
        public String toString() {
            return String.format("RuleSummary(%d, %d, %d)", totalTimeNs, numInvocations, numEffectiveInvocations);
        }
    }

    /**
     * Summary of a phase, with start time and end time so we can construct a timeline.
     */
    public static class PhaseSummary {
        public final long startTimeMs;
        public final long endTimeMs;

        public PhaseSummary(long startTimeMs, long endTimeMs) {
            this.startTimeMs = startTimeMs;
            this.endTimeMs = endTimeMs;
        }

        public long durationMs() {
            return endTimeMs - startTimeMs;
        }

        @Override
        public String toString() {
            return String.format("PhaseSummary(%d, %d)", startTimeMs, endTimeMs);
        }
    }

    /**
     * A thread local variable to implicitly pass the tracker around. This assumes the query planner
     * is single-threaded, and avoids passing the same tracker context in every function call.
     */
    private static final ThreadLocal<QueryPlanningTracker> localTracker = new ThreadLocal<QueryPlanningTracker>() {
        @Override
        protected QueryPlanningTracker initialValue() {
            return null;
        }
    };

    /** Returns the current tracker in scope, based on the thread local variable. */
    public static Option<QueryPlanningTracker> get() {
        return Option.option(localTracker.get());
    }

    /** Sets the current tracker for the execution of function f. We assume f is single-threaded. */
    public static <T> T withTracker(QueryPlanningTracker tracker, Supplier<T> f) {
        QueryPlanningTracker originalTracker = localTracker.get();
        localTracker.set(tracker);
        try {
            return f.get();
        } finally {
            localTracker.set(originalTracker);
        }
    }

    /**
     * Callbacks after planning phase completion.
     */
    public abstract static class QueryPlanningTrackerCallback {
        /**
         * Called when query has been analyzed.
         *
         * @param tracker tracker that triggered the callback.
         * @param analyzedPlan The plan after analysis,
         *                     see @com.jipple.sql.catalyst.analysis.Analyzer
         */
        public abstract void analyzed(QueryPlanningTracker tracker, LogicalPlan analyzedPlan);

        /**
         * Called when query is ready for execution.
         * This is after analysis for eager commands and after planning for other queries.
         * @param tracker tracker that triggered the callback.
         */
        public abstract void readyForExecution(QueryPlanningTracker tracker);
    }

    /**
     * @param trackerCallback Callback to be notified of planning phase completion.
     */
    private final Option<QueryPlanningTrackerCallback> trackerCallback;

    // Mapping from the name of a rule to a rule's summary.
    // Use a Java HashMap for less overhead.
    private final Map<String, RuleSummary> rulesMap = new HashMap<>();

    // From a phase to its start time and end time, in ms.
    private final Map<String, PhaseSummary> phasesMap = new HashMap<>();

    private boolean readyForExecution = false;

    public QueryPlanningTracker() {
        this(Option.none());
    }

    public QueryPlanningTracker(Option<QueryPlanningTrackerCallback> trackerCallback) {
        this.trackerCallback = trackerCallback;
    }

    /**
     * Measure the start and end time of a phase. Note that if this function is called multiple
     * times for the same phase, the recorded start time will be the start time of the first call,
     * and the recorded end time will be the end time of the last call.
     */
    public <T> T measurePhase(String phase, Supplier<T> f) {
        long startTime = System.currentTimeMillis();
        T ret = f.get();
        long endTime = System.currentTimeMillis();

        if (phasesMap.containsKey(phase)) {
            PhaseSummary oldSummary = phasesMap.get(phase);
            phasesMap.put(phase, new PhaseSummary(oldSummary.startTimeMs, endTime));
        } else {
            phasesMap.put(phase, new PhaseSummary(startTime, endTime));
        }
        return ret;
    }

    /**
     * Set when the query has been analysed.
     * Can be called multiple times upon plan change.
     * @param analyzedPlan The plan after analysis,
     *                     see @com.jipple.sql.catalyst.analysis.Analyzer
     */
    public void setAnalyzed(LogicalPlan analyzedPlan) {
        trackerCallback.forEach(callback -> callback.analyzed(this, analyzedPlan));
    }

    /**
     * Set when the query is ready for execution. This is after analysis for
     * eager commands and after planning for other queries.
     * see @link com.jipple.sql.execution.CommandExecutionMode
     * When called multiple times, ignores subsequent call.
     */
    void setReadyForExecution() {
        if (!readyForExecution) {
            readyForExecution = true;
            trackerCallback.forEach(callback -> callback.readyForExecution(this));
        }
    }

    /**
     * Record a specific invocation of a rule.
     *
     * @param rule name of the rule
     * @param timeNs time taken to run this invocation
     * @param effective whether the invocation has resulted in a plan change
     */
    public void recordRuleInvocation(String rule, long timeNs, boolean effective) {
        RuleSummary s = rulesMap.get(rule);
        if (s == null) {
            s = new RuleSummary();
            rulesMap.put(rule, s);
        }

        s.totalTimeNs += timeNs;
        s.numInvocations += 1;
        s.numEffectiveInvocations += (effective ? 1 : 0);
    }

    // ------------ reporting functions below ------------

    public Map<String, RuleSummary> rules() {
        return new HashMap<>(rulesMap);
    }

    public Map<String, PhaseSummary> phases() {
        return new HashMap<>(phasesMap);
    }

    /**
     * Returns the top k most expensive rules (as measured by time). If k is larger than the rules
     * seen so far, return all the rules. If there is no rule seen so far or k <= 0, return empty list.
     */
    public List<Map.Entry<String, RuleSummary>> topRulesByTime(int k) {
        if (k <= 0) {
            return new ArrayList<>();
        } else {
            Comparator<Map.Entry<String, RuleSummary>> orderingByTime = 
                Comparator.comparingLong(e -> e.getValue().totalTimeNs);
            BoundedPriorityQueue<Map.Entry<String, RuleSummary>> q = 
                new BoundedPriorityQueue<>(k, orderingByTime);
            q.addAll(rulesMap.entrySet());
            return StreamSupport.stream(q.spliterator(), false)
                .sorted(Comparator.comparingLong((Map.Entry<String, RuleSummary> r) -> -r.getValue().totalTimeNs))
                .collect(Collectors.toList());
        }
    }
}

