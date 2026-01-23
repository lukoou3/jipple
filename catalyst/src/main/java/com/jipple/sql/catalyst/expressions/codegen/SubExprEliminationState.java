package com.jipple.sql.catalyst.expressions.codegen;

import java.util.Collections;
import java.util.List;


public class SubExprEliminationState {
    public ExprCode eval;
    public List<SubExprEliminationState> children;

    /**
     * State used for subexpression elimination.
     *
     * @param eval The source code for evaluating the subexpression.
     * @param children The sequence of subexpressions as the children expressions. Before
     *                 evaluating this subexpression, we should evaluate all children
     *                 subexpressions first.
     */
    public SubExprEliminationState(ExprCode eval, List<SubExprEliminationState> children) {
        this.eval = eval;
        this.children = children;
    }

    public static SubExprEliminationState apply(ExprCode eval) {
        return new SubExprEliminationState(eval, Collections.emptyList());
    }

    public static SubExprEliminationState apply(ExprCode eval, List<SubExprEliminationState> children) {
        List<SubExprEliminationState> reversed = new java.util.ArrayList<>(children);
        Collections.reverse(reversed);
        return new SubExprEliminationState(eval, reversed);
    }
}

