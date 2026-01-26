package com.jipple.sql.catalyst.expressions;

/**
 * An expression that contains conditional expression branches, so not all branches will be hit.
 * All optimization should be careful with the evaluation order.
 */
public interface ConditionalExpression {
    static boolean conditionalFoldable(Expression self) {
        return self.children().stream().allMatch(Expression::foldable);
    }
}
