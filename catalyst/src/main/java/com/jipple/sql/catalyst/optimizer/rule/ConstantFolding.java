package com.jipple.sql.catalyst.optimizer.rule;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.ConditionalExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.RichExpression;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;

public class ConstantFolding extends Rule<LogicalPlan> {

    private Expression constantFolding(Expression e, boolean isConditionalBranch) {
        if (e instanceof ConditionalExpression && !ConditionalExpression.conditionalFoldable(e)) {
            return e.mapChildren(c -> constantFolding(c, true));
        }
        // Skip redundant folding of literals. This rule is technically not necessary. Placing this
        // here avoids running the next rule for Literal values, which would create a new Literal
        // object and running eval unnecessarily.
        if (e instanceof Literal l) {
            return l;
        }
        // case e if e.getTagValue(FAILED_TO_EVALUATE).isDefined => e
        // Fold expressions that are foldable.
        if (e.foldable() && !(e instanceof RichExpression)) {
            try {
                return Literal.create(e.freshCopyIfContainsStatefulExpression().eval(InternalRow.EMPTY), e.dataType());
            } catch (Exception ex) {
                // When doing constant folding inside conditional expressions, we should not fail
                // during expression evaluation, as the branch we are evaluating may not be reached at
                // runtime, and we shouldn't fail the query, to match the original behavior.
                if (isConditionalBranch) {
                    return e;
                } else {
                    throw ex;
                }
            }
        }
        return e.mapChildren(c -> constantFolding(c, isConditionalBranch));
    }

    @Override
    public LogicalPlan apply(LogicalPlan plan) {
        return plan.transformDown(q -> q.mapExpressions(e -> constantFolding(e, false)));
    }
}
