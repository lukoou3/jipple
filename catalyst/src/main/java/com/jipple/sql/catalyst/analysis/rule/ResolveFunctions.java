package com.jipple.sql.catalyst.analysis.rule;

import com.jipple.sql.catalyst.analysis.SimpleFunctionRegistry;
import com.jipple.sql.catalyst.analysis.unresolved.UnresolvedFunction;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.identifier.FunctionIdentifier;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;

import java.util.List;

public class ResolveFunctions extends Rule<LogicalPlan> {
    private final SimpleFunctionRegistry functionRegistry;

    public ResolveFunctions(SimpleFunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.transformUp(p ->
            p.transformExpressions(e -> {
                // Skip until children are resolved.
                if (!e.childrenResolved()) {
                    return e;
                }
                if (e instanceof UnresolvedFunction u) {
                    List<String> nameParts = u.nameParts;
                    List<Expression> arguments = u.arguments;
                    Expression func = functionRegistry.lookupFunction(new FunctionIdentifier(nameParts.get(nameParts.size() - 1)), arguments);
                    return func;
                }
                return e;
            })
        );
    }
}
