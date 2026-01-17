package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.expressions.named.AttributeReference;
import com.jipple.sql.catalyst.expressions.named.ExprId;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class TypeCoercionRule extends Rule<LogicalPlan> {
    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        LogicalPlan newPlan = coerceTypes(plan);
        if (plan.fastEquals(newPlan)) {
            return plan;
        } else {
            return propagateTypes(newPlan);
        }
    }

    public abstract LogicalPlan coerceTypes(LogicalPlan plan);

    private LogicalPlan propagateTypes(LogicalPlan plan) {
        return plan.transformUp(p -> {
            // No propagation required for leaf nodes.
            if(p.children().isEmpty()) {
                return p;
            }
            // Don't propagate types from unresolved children.
            if (!p.childrenResolved()) {
                return p;
            }
            Map<ExprId, Attribute> inputMap = p.inputSet().toSeq().stream().collect(Collectors.toMap(a -> a.exprId(), a -> a));
            return p.transformExpressions(e -> {
                if(e instanceof AttributeReference a) {
                    Attribute newType = inputMap.get(a.exprId());
                    if (newType == null) {
                        return a;
                    }
                    // Leave the same if the dataTypes match.
                    if(newType.dataType().equals(a.dataType()) && a.nullable() == newType.nullable()) {
                        return a;
                    }
                    // 不知道什么情况会出现这种情况
                    System.out.println("Promoting " + a + "from " + a.dataType() + " to " + newType.dataType() + " in " + p.simpleString(25) );
                    return newType;
                }
                return e;
            });
        });
    }

}
