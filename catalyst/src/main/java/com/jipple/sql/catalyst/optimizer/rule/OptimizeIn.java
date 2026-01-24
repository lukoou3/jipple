package com.jipple.sql.catalyst.optimizer.rule;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.ExpressionSet;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.complextype.CreateNamedStruct;
import com.jipple.sql.catalyst.expressions.predicate.In;
import com.jipple.sql.catalyst.expressions.predicate.InSet;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Optimize IN predicates:
 * 1. Converts the predicate to false when the list is empty and
 *    the value is not nullable.
 * 2. Removes literal repetitions.
 * 3. Replaces [[In (value, seq[Literal])]] with optimized version
 *    [[InSet (value, HashSet[Literal])]] which is much faster.
 */
public class OptimizeIn extends Rule<LogicalPlan> {
    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.transformAllExpressions(expr -> {
            if (expr instanceof In in && in.list.isEmpty()) {
                return Literal.FalseLiteral;
            }
            if (expr instanceof In in && in.inSetConvertible()) {
                List<Expression> newList = toExpressionList(ExpressionSet.apply(in.list));
                if (newList.size() == 1
                        && !(in.value instanceof CreateNamedStruct)
                        && !(newList.get(0) instanceof CreateNamedStruct)) {
                    return new com.jipple.sql.catalyst.expressions.predicate.EqualTo(in.value, newList.get(0));
                } else if (newList.size() > conf().optimizerInSetConversionThreshold()) {
                    Set<Object> hSet = new HashSet<>();
                    for (Expression e : newList) {
                        hSet.add(e.eval(InternalRow.EMPTY));
                    }
                    return new InSet(in.value, hSet);
                } else if (newList.size() < in.list.size()) {
                    return new In(in.value, newList);
                } else {
                    return expr;
                }
            }
            return expr;
        });
    }

    private List<Expression> toExpressionList(ExpressionSet set) {
        List<Expression> result = new ArrayList<>();
        for (Expression expr : set) {
            result.add(expr);
        }
        return result;
    }

}
