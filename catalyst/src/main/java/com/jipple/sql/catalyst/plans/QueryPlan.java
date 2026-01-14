package com.jipple.sql.catalyst.plans;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.trees.TreeNode;

import java.util.ArrayList;
import java.util.List;

public abstract class QueryPlan<PlanType extends QueryPlan<PlanType>> extends TreeNode<PlanType> {





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

    private void seqAddToExpressions(List<Expression> expressions, Iterable<Object> seq) {
        for (Object o : seq) {
            if (o instanceof Expression expr) {
                expressions.add(expr);
            } else if (o instanceof Iterable iterable) {
                seqAddToExpressions(expressions, iterable);
            }
        }
    }



}
