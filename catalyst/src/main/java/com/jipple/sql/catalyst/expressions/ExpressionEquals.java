package com.jipple.sql.catalyst.expressions;

import java.util.List;
import java.util.Objects;

/**
 * Wrapper around an Expression that provides semantic equality.
 */
public class ExpressionEquals {
    public final Expression expr;
    private final int height;

    public ExpressionEquals(Expression expr) {
        this.expr = expr;
        this.height = getHeight(expr);
    }

    private int getHeight(Expression tree) {
        List<Expression> children = tree.children();
        if (children.isEmpty()) {
            return 1;
        }
        return children.stream()
                .mapToInt(this::getHeight)
                .max()
                .orElse(0) + 1;
    }

    // This is used to do a fast pre-check for child-parent relationship. For example, expr1 can
    // only be a parent of expr2 if expr1.height is larger than expr2.height.
    public int height() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionEquals that = (ExpressionEquals) o;
        return expr.semanticEquals(that.expr) && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr.semanticHash(), height);
    }
}

