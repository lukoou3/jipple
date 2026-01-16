package com.jipple.sql.catalyst.rules;

/**
 * Metrics about rule execution.
 * This class holds aggregated statistics about rule execution time and counts.
 */
public class QueryExecutionMetrics {
    public final long time;
    public final long numRuns;
    public final long numEffectiveRuns;
    public final long timeEffective;

    public QueryExecutionMetrics(long time, long numRuns, long numEffectiveRuns, long timeEffective) {
        this.time = time;
        this.numRuns = numRuns;
        this.numEffectiveRuns = numEffectiveRuns;
        this.timeEffective = timeEffective;
    }

    /**
     * Subtracts another metrics object from this one, returning a new QueryExecutionMetrics
     * with the difference. This is useful for calculating metrics for a specific execution
     * by subtracting the metrics before execution from the metrics after execution.
     */
    public QueryExecutionMetrics subtract(QueryExecutionMetrics other) {
        return new QueryExecutionMetrics(
            this.time - other.time,
            this.numRuns - other.numRuns,
            this.numEffectiveRuns - other.numEffectiveRuns,
            this.timeEffective - other.timeEffective
        );
    }

    @Override
    public String toString() {
        return String.format("QueryExecutionMetrics(time=%d, numRuns=%d, numEffectiveRuns=%d, timeEffective=%d)",
                time, numRuns, numEffectiveRuns, timeEffective);
    }
}

