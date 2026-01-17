package com.jipple.sql.catalyst.plans;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.trees.CurrentOrigin;
import com.jipple.sql.catalyst.trees.TreeNode;
import com.jipple.sql.types.DataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class QueryPlan<PlanType extends QueryPlan<PlanType>> extends TreeNode<PlanType> {

    public abstract List<Attribute> output();



    public final List<Expression> expressions() {
        List<Expression> expressions = new ArrayList<>();
        Object[] args = args();
        for (Object arg : args) {
            if (arg instanceof Expression expr) {
                expressions.add(expr);
            } else if (arg instanceof Option option) {
                if (option.isDefined()) {
                    seqAddToExpressions(expressions, List.of(option.get()));
                }
            } else if (arg instanceof Iterable iterable) {
                seqAddToExpressions(expressions, iterable);
            }
        }
        return expressions;
    }

    private void seqAddToExpressions(List<Expression> expressions, Iterable<?> seq) {
        for (Object o : seq) {
            if (o instanceof Expression expr) {
                expressions.add(expr);
            } else if (o instanceof Iterable iterable) {
                seqAddToExpressions(expressions, iterable);
            }
        }
    }

    /**
     * Apply a map function to each expression present in this query operator, and return a new
     * query operator based on the mapped expressions.
     */
    public final PlanType mapExpressions(Function<Expression, Expression> f) {
        boolean[] changed = new boolean[]{false};

        Function<Expression, Expression> transformExpression = e -> {
            Expression newExpr = CurrentOrigin.withOrigin(e.origin(), () -> f.apply(e));
            if (newExpr.fastEquals(e)) {
                return e;
            }
            changed[0] = true;
            return newExpr;
        };

        Object[] args = args();
        Object[] newArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            newArgs[i] = recursiveTransformExpression(args[i], transformExpression);
        }

        return changed[0] ? makeCopy(newArgs) : self();
    }

    private Object recursiveTransformExpression(Object arg, Function<Expression, Expression> transformExpression) {
        if (arg instanceof Expression expr) {
            return transformExpression.apply(expr);
        }
        if (arg instanceof Option option) {
            if (option.isDefined()) {
                return Option.some(recursiveTransformExpression(option.get(), transformExpression));
            }
            return Option.none();
        }
        if (arg instanceof Map<?, ?>) {
            return arg;
        }
        if (arg instanceof DataType) {
            return arg;
        }
        if (arg instanceof List list) {
            List<Object> mapped = new ArrayList<>();
            for (Object value : list) {
                mapped.add(recursiveTransformExpression(value, transformExpression));
            }
            return mapped;
        }
        return arg;
    }

    /**
     * A prefix string used when printing the plan.
     *
     * We use "!" to indicate an invalid plan, and "'" to indicate an unresolved plan.
     */
    protected String statePrefix() {
        // if (missingInput.nonEmpty && children.nonEmpty) "!" else ""
        return "";
    }

    @Override
    public String simpleString(int maxFields) {
        return statePrefix() + super.simpleString(maxFields);
    }

    @Override
    public String verboseString(int maxFields) {
        return simpleString(maxFields);
    }

    @Override
    public String simpleStringWithNodeId() {
        return nodeName();
    }

}
