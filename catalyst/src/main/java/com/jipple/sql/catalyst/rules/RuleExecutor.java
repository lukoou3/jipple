package com.jipple.sql.catalyst.rules;

import com.jipple.collection.Option;
import com.jipple.error.JippleException;
import com.jipple.sql.catalyst.QueryPlanningTracker;
import com.jipple.sql.catalyst.trees.TreeNode;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

import static com.jipple.sql.catalyst.util.DateTimeConstants.NANOS_PER_SECOND;
import static com.jipple.sql.catalyst.util.JippleStringUtils.sideBySide;

/**
 * Abstract class for executing rules on a plan tree.
 * 
 * This class provides the framework for applying transformation rules to a plan tree.
 * It tracks execution metrics, logs plan changes, and supports different execution strategies
 * (Once, FixedPoint).
 */
public abstract class RuleExecutor<TreeType extends TreeNode<?>> {
    private static final Logger LOG = LoggerFactory.getLogger(RuleExecutor.class);

    // QueryExecutionMetering tracks metrics for rule execution (time, number of runs, etc.)
    protected static QueryExecutionMetering queryExecutionMeter = new QueryExecutionMetering();

    /**
     * Dump statistics about time spent running specific rules.
     */
    public static String dumpTimeSpent() {
        return queryExecutionMeter.dumpTimeSpent();
    }

    /**
     * Resets statistics about time spent running specific rules.
     */
    public static void resetMetrics() {
        queryExecutionMeter.resetMetrics();
    }

    /**
     * Gets the current execution metrics.
     */
    public static QueryExecutionMetrics getCurrentMetrics() {
        return queryExecutionMeter.getMetrics();
    }

    /**
     * Logger for plan changes during rule execution.
     * This class logs rule applications and batch results for debugging purposes.
     */
    public static class PlanChangeLogger<TreeType extends TreeNode<?>> {
        private static final Logger LOG = LoggerFactory.getLogger(PlanChangeLogger.class);
        private final String logLevel = "DEBUG"; // SQLConf.get.planChangeLogLevel
        private final Option<List<String>> logRules = Option.none(); // SQLConf.get.planChangeRules
        private final Option<List<String>> logBatches = Option.none(); // SQLConf.get.planChangeBatches

        /**
         * Logs the application of a rule, showing the plan before and after.
         */
        public void logRule(String ruleName, TreeType oldPlan, TreeType newPlan) {
            if (!newPlan.fastEquals(oldPlan)) {
                if (logRules.isEmpty() || logRules.get().contains(ruleName)) {
                    String message = String.format(
                        "\n=== Applying Rule %s ===\n%s",
                        ruleName,
                        sideBySide(oldPlan.treeString(), newPlan.treeString())
                    );
                    logBasedOnLevel(() -> message);
                }
            }
        }

        /**
         * Logs the result of a batch execution.
         */
        public void logBatch(String batchName, TreeType oldPlan, TreeType newPlan) {
            if (logBatches.isEmpty() || logBatches.get().contains(batchName)) {
                String message;
                if (!oldPlan.fastEquals(newPlan)) {
                    message = String.format(
                        "\n=== Result of Batch %s ===\n%s",
                        batchName,
                        sideBySide(oldPlan.treeString(), newPlan.treeString())
                    );
                } else {
                    message = String.format("Batch %s has no effect.", batchName);
                }
                logBasedOnLevel(() -> message);
            }
        }

        /**
         * Logs execution metrics.
         */
        public void logMetrics(QueryExecutionMetrics metrics) {
            double totalTime = metrics.time / (double) NANOS_PER_SECOND;
            double totalTimeEffective = metrics.timeEffective / (double) NANOS_PER_SECOND;
            String message = String.format(
                "\n=== Metrics of Executed Rules ===\n" +
                "Total number of runs: %d\n" +
                "Total time: %f seconds\n" +
                "Total number of effective runs: %d\n" +
                "Total time of effective runs: %f seconds\n",
                metrics.numRuns,
                totalTime,
                metrics.numEffectiveRuns,
                totalTimeEffective
            );
            logBasedOnLevel(() -> message);
        }

        /**
         * Logs a message based on the configured log level.
         */
        private void logBasedOnLevel(Supplier<String> messageSupplier) {
            String message = messageSupplier.get();
            switch (logLevel) {
                case "TRACE":
                    LOG.trace(message);
                    break;
                case "DEBUG":
                    LOG.debug(message);
                    break;
                case "INFO":
                    LOG.info(message);
                    break;
                case "WARN":
                    LOG.warn(message);
                    break;
                case "ERROR":
                    LOG.error(message);
                    break;
                default:
                    LOG.debug(message);
                    break;
            }
        }
    }

    /**
     * An execution strategy for rules that indicates the maximum number of executions.
     * If the execution reaches fix point (i.e. converge) before maxIterations, it will stop.
     */
    public abstract static class Strategy {
        /** The maximum number of executions. */
        public abstract int maxIterations();

        /** Whether to throw exception when exceeding the maximum number. */
        public boolean errorOnExceed() {
            return false;
        }

        /** The key of SQLConf setting to tune maxIterations */
        public String maxIterationsSetting() {
            return null;
        }
    }

    /**
     * A strategy that is run once and idempotent.
     */
    public static final Strategy Once = new Strategy() {
        @Override
        public int maxIterations() {
            return 1;
        }
    };

    /**
     * A strategy that runs until fix point or maxIterations times, whichever comes first.
     * Especially, a FixedPoint(1) batch is supposed to run only once.
     */
    public static class FixedPoint extends Strategy {
        private final int maxIterations;
        private final boolean errorOnExceed;
        private final String maxIterationsSetting;

        public FixedPoint(int maxIterations) {
            this(maxIterations, false, null);
        }

        public FixedPoint(int maxIterations, boolean errorOnExceed, String maxIterationsSetting) {
            this.maxIterations = maxIterations;
            this.errorOnExceed = errorOnExceed;
            this.maxIterationsSetting = maxIterationsSetting;
        }

        @Override
        public int maxIterations() {
            return maxIterations;
        }

        @Override
        public boolean errorOnExceed() {
            return errorOnExceed;
        }

        @Override
        public String maxIterationsSetting() {
            return maxIterationsSetting;
        }
    }

    /**
     * A batch of rules.
     */
    public static class Batch {
        public final String name;
        public final Strategy strategy;
        public final List<Rule<?>> rules;

        public Batch(String name, Strategy strategy, Rule<?>... rules) {
            this.name = name;
            this.strategy = strategy;
            this.rules = Arrays.asList(rules);
        }
    }

    /**
     * Defines a sequence of rule batches, to be overridden by the implementation.
     */
    protected abstract List<Batch> batches();

    /**
     * Once batches that are excluded in the idempotence checker.
     */
    protected Set<String> excludedOnceBatches() {
        return Collections.emptySet();
    }

    /**
     * Defines a validate function that validates the plan changes after the execution of each rule,
     * to make sure these rules make valid changes to the plan. For example, we can check whether
     * a plan is still resolved after each rule in Optimizer, so that we can catch rules that
     * turn the plan into unresolved.
     * 
     * @param previousPlan the plan before rule application
     * @param currentPlan the plan after rule application
     * @return Some(error message) if validation fails, None otherwise
     */
    protected Option<String> validatePlanChanges(TreeType previousPlan, TreeType currentPlan) {
        return Option.none();
    }

    /**
     * Util method for checking whether a plan remains the same if re-optimized.
     * This is used to verify that Once strategy batches are idempotent.
     */
    @SuppressWarnings("unchecked")
    private void checkBatchIdempotence(Batch batch, TreeType plan) {
        TreeType reOptimized = plan;
        for (Rule<?> rule : batch.rules) {
            reOptimized = (TreeType) ((Rule<TreeType>) rule).apply(reOptimized);
        }
        if (!plan.fastEquals(reOptimized)) {
            throw QueryExecutionErrors.onceStrategyIdempotenceIsBrokenForBatchError(batch.name, plan, reOptimized);
        }
    }

    /**
     * Executes the batches of rules defined by the subclass, and also tracks timing info for each
     * rule using the provided tracker.
     * 
     * @param plan the plan to transform
     * @param tracker the query planning tracker to record metrics
     * @return the transformed plan
     * @see #execute(TreeType)
     */
    public TreeType executeAndTrack(TreeType plan, QueryPlanningTracker tracker) {
        return QueryPlanningTracker.withTracker(tracker, () -> execute(plan));
    }

    /**
     * Executes the batches of rules defined by the subclass. The batches are executed serially
     * using the defined execution strategy. Within each batch, rules are also executed serially.
     * 
     * This is the core execution loop that:
     * 1. Iterates through each batch
     * 2. For each batch, applies rules until fix point or max iterations
     * 3. Tracks metrics and logs plan changes
     * 4. Validates plan changes if enabled
     * 
     * @param plan the initial plan to transform
     * @return the final transformed plan
     */
    @SuppressWarnings("unchecked")
    public TreeType execute(TreeType plan) {
        TreeType curPlan = plan;
        QueryExecutionMetering queryExecutionMetrics = queryExecutionMeter;
        PlanChangeLogger<TreeType> planChangeLogger = new PlanChangeLogger<>();
        Option<QueryPlanningTracker> tracker = QueryPlanningTracker.get();
        QueryExecutionMetrics beforeMetrics = getCurrentMetrics();

        boolean enableValidation = false; // SQLConf.get.getConf(SQLConf.PLAN_CHANGE_VALIDATION)
        
        // Validate the initial input.
        if (Utils.isTesting() || enableValidation) {
            Option<String> validationResult = validatePlanChanges(plan, plan);
            if (validationResult.isDefined()) {
                String ruleExecutorName = this.getClass().getName().replaceAll("\\$$", "");
                throw new JippleException(
                    "PLAN_VALIDATION_FAILED_RULE_EXECUTOR",
                    Map.of("ruleExecutor", ruleExecutorName, "reason", validationResult.get()),
                    null);
            }
        }

        // Execute each batch
        for (Batch batch : batches()) {
            TreeType batchStartPlan = curPlan;
            int iteration = 1;
            TreeType lastPlan = curPlan;
            boolean continueLoop = true;

            // Run until fix point (or the max number of iterations as specified in the strategy).
            while (continueLoop) {
                // Apply all rules in the batch sequentially
                for (Rule<?> rule : batch.rules) {
                    long startTime = System.nanoTime();
                    TreeType result = (TreeType) ((Rule<TreeType>) rule).apply(curPlan);
                    long runTime = System.nanoTime() - startTime;
                    boolean effective = !result.fastEquals(curPlan);

                    if (effective) {
                        queryExecutionMetrics.incNumEffectiveExecution(rule.ruleName);
                        queryExecutionMetrics.incTimeEffectiveExecutionBy(rule.ruleName, runTime);
                        planChangeLogger.logRule(rule.ruleName, curPlan, result);
                        
                        // Run the plan changes validation after each rule.
                        if (Utils.isTesting() || enableValidation) {
                            Option<String> validationResult = validatePlanChanges(curPlan, result);
                            if (validationResult.isDefined()) {
                                throw new JippleException(
                                    "PLAN_VALIDATION_FAILED_RULE_IN_BATCH",
                                    Map.of(
                                        "rule", rule.ruleName,
                                        "batch", batch.name,
                                        "reason", validationResult.get()),
                                    null);
                            }
                        }
                    }
                    
                    queryExecutionMetrics.incExecutionTimeBy(rule.ruleName, runTime);
                    queryExecutionMetrics.incNumExecution(rule.ruleName);

                    // Record timing information using QueryPlanningTracker
                    tracker.forEach(t -> t.recordRuleInvocation(rule.ruleName, runTime, effective));

                    curPlan = result;
                }
                
                iteration++;
                if (iteration > batch.strategy.maxIterations()) {
                    // Only log if this is a rule that is supposed to run more than once.
                    if (iteration != 2) {
                        String endingMsg = batch.strategy.maxIterationsSetting() == null
                            ? "."
                            : String.format(", please set '%s' to a larger value.", batch.strategy.maxIterationsSetting());
                        String message = String.format(
                            "Max iterations (%d) reached for batch %s%s",
                            iteration - 1,
                            batch.name,
                            endingMsg
                        );
                        if (Utils.isTesting() || batch.strategy.errorOnExceed()) {
                            throw new RuntimeException(message);
                        } else {
                            LOG.warn(message);
                        }
                    }
                    
                    // Check idempotence for Once batches.
                    if (batch.strategy.equals(Once) &&
                        Utils.isTesting() && !excludedOnceBatches().contains(batch.name)) {
                        checkBatchIdempotence(batch, curPlan);
                    }
                    continueLoop = false;
                }

                // Check if we've reached fix point (plan no longer changes)
                if (curPlan.fastEquals(lastPlan)) {
                    LOG.debug(String.format("Fixed point reached for batch %s after %d iterations.", batch.name, iteration - 1));
                    continueLoop = false;
                }
                lastPlan = curPlan;
            }

            planChangeLogger.logBatch(batch.name, batchStartPlan, curPlan);
        }
        
        // Calculate and log final metrics
        QueryExecutionMetrics finalMetrics = getCurrentMetrics();
        QueryExecutionMetrics diffMetrics = finalMetrics.subtract(beforeMetrics);
        planChangeLogger.logMetrics(diffMetrics);

        return curPlan;
    }


}

