package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.codegen.CodegenFallback;
import com.jipple.sql.catalyst.expressions.objects.LambdaVariable;
import com.jipple.sql.catalyst.expressions.predicate.And;
import com.jipple.sql.catalyst.expressions.predicate.Or;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used to compute equality of (sub)expression trees. Expressions can be added
 * to this class and they subsequently query for expression equality. Expression trees are
 * considered equal if for the same input(s), the same result is produced.
 */
public class EquivalentExpressions {
    private final boolean skipForShortcutEnable;
    
    // For each expression, the set of equivalent expressions.
    private final Map<ExpressionEquals, ExpressionStats> equivalenceMap = new HashMap<>();

    public EquivalentExpressions() {
        this(false);
    }

    public EquivalentExpressions(boolean skipForShortcutEnable) {
        this.skipForShortcutEnable = skipForShortcutEnable;
    }

    /**
     * Adds each expression to this data structure, grouping them with existing equivalent
     * expressions. Non-recursive.
     * Returns true if there was already a matching expression.
     */
    public boolean addExpr(Expression expr) {
        if (supportedExpression(expr)) {
            return updateExprInMap(expr, equivalenceMap, 1);
        } else {
            return false;
        }
    }

    /**
     * Adds or removes an expression to/from the map and updates `useCount`.
     * Returns true
     * - if there was a matching expression in the map before add or
     * - if there remained a matching expression in the map after remove (`useCount` remained > 0)
     * to indicate there is no need to recurse in `updateExprTree`.
     */
    private boolean updateExprInMap(
            Expression expr,
            Map<ExpressionEquals, ExpressionStats> map,
            int useCount) {
        if (expr.deterministic()) {
            ExpressionEquals wrapper = new ExpressionEquals(expr);
            ExpressionStats stats = map.get(wrapper);
            if (stats != null) {
                stats.useCount += useCount;
                if (stats.useCount > 0) {
                    return true;
                } else if (stats.useCount == 0) {
                    map.remove(wrapper);
                    return false;
                } else {
                    // Should not happen
                    throw new IllegalStateException(
                            "Cannot update expression: " + expr + " in map: " + map + " with use count: " + useCount);
                }
            } else {
                if (useCount > 0) {
                    map.put(wrapper, new ExpressionStats(expr, useCount));
                }
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Adds or removes only expressions which are common in each of given expressions, in a recursive
     * way.
     * For example, given two expressions `(a + (b + (c + 1)))` and `(d + (e + (c + 1)))`, the common
     * expression `(c + 1)` will be added into `equivalenceMap`.
     *
     * Note that as we don't know in advance if any child node of an expression will be common across
     * all given expressions, we compute local equivalence maps for all given expressions and filter
     * only the common nodes.
     * Those common nodes are then removed from the local map and added to the final map of
     * expressions.
     */
    private void updateCommonExprs(
            List<Expression> exprs,
            Map<ExpressionEquals, ExpressionStats> map,
            int useCount) {
        assert exprs.size() > 1;
        Map<ExpressionEquals, ExpressionStats> localEquivalenceMap = new HashMap<>();
        updateExprTree(exprs.get(0), localEquivalenceMap, 1);

        for (int i = 1; i < exprs.size(); i++) {
            Expression expr = exprs.get(i);
            Map<ExpressionEquals, ExpressionStats> otherLocalEquivalenceMap = new HashMap<>();
            updateExprTree(expr, otherLocalEquivalenceMap, 1);
            // Filter to keep only common keys
            localEquivalenceMap = localEquivalenceMap.entrySet().stream()
                    .filter(entry -> otherLocalEquivalenceMap.containsKey(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        // Start with the highest expression, remove it from `localEquivalenceMap` and add it to `map`.
        // The remaining highest expression in `localEquivalenceMap` is also common expression so loop
        // until `localEquivalenceMap` is not empty.
        Optional<ExpressionStats> statsOption = localEquivalenceMap.isEmpty() ?
                Optional.empty() :
                Optional.of(localEquivalenceMap.entrySet().stream()
                        .max(Comparator.comparingInt(e -> e.getKey().height()))
                        .map(Map.Entry::getValue)
                        .orElse(null));

        while (statsOption.isPresent()) {
            ExpressionStats stats = statsOption.get();
            updateExprTree(stats.expr, localEquivalenceMap, -stats.useCount);
            updateExprTree(stats.expr, map, useCount);

            statsOption = localEquivalenceMap.isEmpty() ?
                    Optional.empty() :
                    Optional.of(localEquivalenceMap.entrySet().stream()
                            .max(Comparator.comparingInt(e -> e.getKey().height()))
                            .map(Map.Entry::getValue)
                            .orElse(null));
        }
    }

    private Expression skipForShortcut(Expression expr) {
        if (skipForShortcutEnable) {
            // The subexpression may not need to eval even if it appears more than once.
            // e.g., `if(or(a, and(b, b)))`, the expression `b` would be skipped if `a` is true.
            if (expr instanceof And) {
                And and = (And) expr;
                return and.left;
            } else if (expr instanceof Or) {
                Or or = (Or) expr;
                return or.left;
            } else {
                return expr;
            }
        } else {
            return expr;
        }
    }

    // There are some special expressions that we should not recurse into all of its children.
    //   1. CodegenFallback: it's children will not be used to generate code (call eval() instead)
    //   2. ConditionalExpression: use its children that will always be evaluated.
    private List<Expression> childrenToRecurse(Expression expr) {
        if (expr instanceof CodegenFallback) {
            return Collections.emptyList();
        }
        // TODO: ConditionalExpression interface not implemented yet, comment out for now
        /*
        else if (expr instanceof ConditionalExpression) {
            ConditionalExpression c = (ConditionalExpression) expr;
            return c.alwaysEvaluatedInputs().stream()
                    .map(this::skipForShortcut)
                    .collect(Collectors.toList());
        }
        */
        else {
            return skipForShortcut(expr).children().stream()
                    .map(this::skipForShortcut)
                    .collect(Collectors.toList());
        }
    }

    // For some special expressions we cannot just recurse into all of its children, but we can
    // recursively add the common expressions shared between all of its children.
    private List<List<Expression>> commonChildrenToRecurse(Expression expr) {
        if (expr instanceof CodegenFallback) {
            return Collections.emptyList();
        }
        // TODO: ConditionalExpression interface not implemented yet, comment out for now
        /*
        else if (expr instanceof ConditionalExpression) {
            ConditionalExpression c = (ConditionalExpression) expr;
            return c.branchGroups();
        }
        */
        else {
            return Collections.emptyList();
        }
    }

    private boolean supportedExpression(Expression e) {
        return !exists(e, expr -> expr instanceof LambdaVariable);
        // TODO: PlanExpression not implemented yet
        // case _: PlanExpression[_] => Utils.isInRunningSparkTask
    }

    /**
     * Checks if any node in the tree matches the predicate.
     */
    private boolean exists(Expression expr, java.util.function.Predicate<Expression> predicate) {
        if (predicate.test(expr)) {
            return true;
        }
        for (Expression child : expr.children()) {
            if (exists(child, predicate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the expression to this data structure recursively. Stops if a matching expression
     * is found. That is, if `expr` has already been added, its children are not added.
     */
    public void addExprTree(Expression expr) {
        addExprTree(expr, equivalenceMap);
    }

    public void addExprTree(Expression expr, Map<ExpressionEquals, ExpressionStats> map) {
        if (supportedExpression(expr)) {
            updateExprTree(expr, map, 1);
        }
    }

    private void updateExprTree(
            Expression expr,
            Map<ExpressionEquals, ExpressionStats> map,
            int useCount) {
        updateExprTree(expr, map, useCount, equivalenceMap);
    }

    private void updateExprTree(
            Expression expr,
            Map<ExpressionEquals, ExpressionStats> map,
            int useCount,
            Map<ExpressionEquals, ExpressionStats> defaultMap) {
        boolean skip = useCount == 0 || expr instanceof LeafExpression;

        if (!skip && !updateExprInMap(expr, map, useCount)) {
            int uc = Integer.signum(useCount);
            for (Expression child : childrenToRecurse(expr)) {
                updateExprTree(child, map, uc, defaultMap);
            }
            for (List<Expression> commonChildren : commonChildrenToRecurse(expr)) {
                if (!commonChildren.isEmpty()) {
                    updateCommonExprs(commonChildren, map, uc);
                }
            }
        }
    }

    /**
     * Returns the state of the given expression in the `equivalenceMap`. Returns None if there is no
     * equivalent expressions.
     */
    public Option<ExpressionStats> getExprState(Expression e) {
        if (supportedExpression(e)) {
            ExpressionStats stats = equivalenceMap.get(new ExpressionEquals(e));
            return stats != null ? Option.some(stats) : Option.none();
        } else {
            return Option.none();
        }
    }

    // Exposed for testing.
    public List<ExpressionStats> getAllExprStates(int count) {
        return equivalenceMap.entrySet().stream()
                .filter(entry -> entry.getValue().useCount > count)
                .sorted(Comparator.comparingInt(e -> e.getKey().height()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public List<ExpressionStats> getAllExprStates() {
        return getAllExprStates(0);
    }

    /**
     * Returns a sequence of expressions that more than one equivalent expressions.
     */
    public List<Expression> getCommonSubexpressions() {
        return getAllExprStates(1).stream()
                .map(stats -> stats.expr)
                .collect(Collectors.toList());
    }

    /**
     * Returns the state of the data structure as a string. If `all` is false, skips sets of
     * equivalent expressions with cardinality 1.
     */
    public String debugString(boolean all) {
        StringBuilder sb = new StringBuilder();
        sb.append("Equivalent expressions:\n");
        equivalenceMap.values().stream()
                .filter(stats -> all || stats.useCount > 1)
                .forEach(stats -> {
                    sb.append("  ").append(stats.expr).append(": useCount = ").append(stats.useCount).append('\n');
                });
        return sb.toString();
    }

    public String debugString() {
        return debugString(false);
    }
}

