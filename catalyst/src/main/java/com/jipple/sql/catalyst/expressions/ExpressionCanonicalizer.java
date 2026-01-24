package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.named.Alias;
import com.jipple.sql.catalyst.rules.Rule;
import com.jipple.sql.catalyst.rules.RuleExecutor;

import java.util.List;

/**
 * Canonicalizes an expression so those that differ only by names can reuse the same code.
 */
public class ExpressionCanonicalizer extends RuleExecutor<Expression> {
    private static final ExpressionCanonicalizer INSTANCE = new ExpressionCanonicalizer();

    public static Expression canonicalize(Expression expression) {
        return INSTANCE.execute(expression);
    }

    @Override
    protected List<Batch> batches() {
        return List.of(
                new Batch("CleanExpressions", new FixedPoint(20), new CleanExpressions())
        );
    }

    /**
     * Removes cosmetic expression wrappers (e.g. Alias).
     */
    private static class CleanExpressions extends Rule<Expression> {
        @Override
        public Expression apply(Expression expression) {
            return expression.transformDown(e -> {
                if (e instanceof Alias alias) {
                    return alias.child;
                }
                return e;
            });
        }
    }
}
