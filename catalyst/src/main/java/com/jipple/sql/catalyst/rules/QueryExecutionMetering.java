package com.jipple.sql.catalyst.rules;

import com.google.common.util.concurrent.AtomicLongMap;

import java.util.Map;
import java.util.stream.Collectors;

import static com.jipple.sql.catalyst.util.DateTimeConstants.NANOS_PER_SECOND;

/**
 * Tracks execution metrics for Catalyst rules.
 * This class maintains thread-safe counters for rule execution time and counts.
 */
public class QueryExecutionMetering {
    private final AtomicLongMap<String> timeMap = AtomicLongMap.create();
    private final AtomicLongMap<String> numRunsMap = AtomicLongMap.create();
    private final AtomicLongMap<String> numEffectiveRunsMap = AtomicLongMap.create();
    private final AtomicLongMap<String> timeEffectiveRunsMap = AtomicLongMap.create();

    /**
     * Resets statistics about time spent running specific rules.
     */
    public void resetMetrics() {
        timeMap.clear();
        numRunsMap.clear();
        numEffectiveRunsMap.clear();
        timeEffectiveRunsMap.clear();
    }

    /**
     * Gets the current execution metrics.
     */
    public QueryExecutionMetrics getMetrics() {
        return new QueryExecutionMetrics(totalTime(), totalNumRuns(), totalNumEffectiveRuns(), totalEffectiveTime());
    }

    /**
     * Returns the total execution time across all rules.
     */
    public long totalTime() {
        return timeMap.sum();
    }

    /**
     * Returns the total number of rule runs.
     */
    public long totalNumRuns() {
        return numRunsMap.sum();
    }

    /**
     * Returns the total number of effective rule runs (runs that changed the plan).
     */
    public long totalNumEffectiveRuns() {
        return numEffectiveRunsMap.sum();
    }

    /**
     * Returns the total effective execution time (time spent on runs that changed the plan).
     */
    public long totalEffectiveTime() {
        return timeEffectiveRunsMap.sum();
    }

    /**
     * Increments the execution time for a rule by the specified delta.
     */
    public void incExecutionTimeBy(String ruleName, long delta) {
        timeMap.addAndGet(ruleName, delta);
    }

    /**
     * Increments the effective execution time for a rule by the specified delta.
     */
    public void incTimeEffectiveExecutionBy(String ruleName, long delta) {
        timeEffectiveRunsMap.addAndGet(ruleName, delta);
    }

    /**
     * Increments the count of effective executions for a rule.
     */
    public void incNumEffectiveExecution(String ruleName) {
        numEffectiveRunsMap.incrementAndGet(ruleName);
    }

    /**
     * Increments the count of executions for a rule.
     */
    public void incNumExecution(String ruleName) {
        numRunsMap.incrementAndGet(ruleName);
    }

    /**
     * Dump statistics about time spent running specific rules.
     * Formats the output as a table showing rule names, execution times, and run counts.
     */
    public String dumpTimeSpent() {
        Map<String, Long> map = timeMap.asMap();
        
        // Find the maximum length of rule names for formatting
        int maxLengthRuleNames;
        if (map.isEmpty()) {
            maxLengthRuleNames = 0;
        } else {
            maxLengthRuleNames = map.keySet().stream()
                    .mapToInt(String::length)
                    .max()
                    .orElse(0);
        }

        // Create column headers with proper padding
        String colRuleName = padTo("Rule", maxLengthRuleNames);
        String colRunTime = padTo("Effective Time / Total Time", 47);
        String colNumRuns = padTo("Effective Runs / Total Runs", 47);

        // Build the metrics rows, sorted by time (descending)
        String ruleMetrics = map.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> {
                    String name = entry.getKey();
                    long time = entry.getValue();
                    long timeEffectiveRun = timeEffectiveRunsMap.get(name);
                    long numRuns = numRunsMap.get(name);
                    long numEffectiveRun = numEffectiveRunsMap.get(name);

                    String ruleName = padTo(name, maxLengthRuleNames);
                    String runtimeValue = padTo(timeEffectiveRun + " / " + time, 47);
                    String numRunValue = padTo(numEffectiveRun + " / " + numRuns, 47);
                    return ruleName + " " + runtimeValue + " " + numRunValue;
                })
                .collect(Collectors.joining("\n", "\n", ""));

        // Format the final output
        return String.format(
            "\n=== Metrics of Analyzer/Optimizer Rules ===\n" +
            "Total number of runs: %d\n" +
            "Total time: %f seconds\n" +
            "\n" +
            "%s %s %s" +
            "%s\n",
            totalNumRuns(),
            totalTime() / (double) NANOS_PER_SECOND,
            colRuleName, colRunTime, colNumRuns,
            ruleMetrics
        );
    }

    /**
     * Pads a string to the specified length with spaces on the right.
     * If the string is longer than the target length, it is returned as-is.
     */
    private String padTo(String str, int length) {
        if (str.length() >= length) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }
}

