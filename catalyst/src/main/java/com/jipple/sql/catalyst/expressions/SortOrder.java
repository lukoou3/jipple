package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.TypeUtils;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.sql.types.DataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An expression that can be used to sort a tuple. This class extends expression primarily so that
 * transformations over expression will descend into its child.
 * {@code sameOrderExpressions} is a set of expressions with the same sort order as the child.
 */
public class SortOrder extends Expression {
    public final Expression child;
    public final SortDirection direction;
    public final NullOrdering nullOrdering;
    public final List<Expression> sameOrderExpressions;

    public SortOrder(
            Expression child,
            SortDirection direction,
            NullOrdering nullOrdering,
            List<Expression> sameOrderExpressions) {
        this.child = child;
        this.direction = direction;
        this.nullOrdering = nullOrdering;
        this.sameOrderExpressions = sameOrderExpressions == null ? List.of() : sameOrderExpressions;
    }

    public SortOrder(Expression child, SortDirection direction, List<Expression> sameOrderExpressions) {
        this(child, direction, direction.defaultNullOrdering(), sameOrderExpressions);
    }

    public SortOrder(Expression child, SortDirection direction) {
        this(child, direction, Collections.emptyList());
    }

    @Override
    public List<Expression> children() {
        List<Expression> children = new ArrayList<>(1 + sameOrderExpressions.size());
        children.add(child);
        children.addAll(sameOrderExpressions);
        return children;
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        return TypeUtils.checkForOrderingExpr(dataType(), prettyName());
    }

    @Override
    public DataType dataType() {
        return child.dataType();
    }

    @Override
    public boolean nullable() {
        return child.nullable();
    }

    @Override
    public String toString() {
        return child + " " + direction.sql() + " " + nullOrdering.sql();
    }

    @Override
    public String sql() {
        return child.sql() + " " + direction.sql() + " " + nullOrdering.sql();
    }

    public boolean isAscending() {
        return direction == SortDirection.ASCENDING;
    }

    public boolean satisfies(SortOrder required) {
        boolean childMatch = children().stream().anyMatch(expr -> expr.semanticEquals(required.child));
        return childMatch && direction == required.direction && nullOrdering == required.nullOrdering;
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        Expression newChild = newChildren.get(0);
        List<Expression> newSameOrder = newChildren.size() > 1
                ? newChildren.subList(1, newChildren.size())
                : Collections.emptyList();
        return new SortOrder(newChild, direction, nullOrdering, newSameOrder);
    }

    @Override
    public Object[] args() {
        return new Object[]{child, direction, nullOrdering, sameOrderExpressions};
    }

    @Override
    public Object eval(InternalRow input) {
        throw QueryExecutionErrors.cannotEvaluateExpressionError(this);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        throw QueryExecutionErrors.cannotGenerateCodeForExpressionError(this);
    }

    public static SortOrder of(Expression child, SortDirection direction, List<Expression> sameOrderExpressions) {
        return new SortOrder(child, direction, sameOrderExpressions);
    }

    public static SortOrder of(Expression child, SortDirection direction) {
        return new SortOrder(child, direction);
    }

    /**
     * Returns if a sequence of SortOrder satisfies another sequence of SortOrder.
     */
    public static boolean orderingSatisfies(List<SortOrder> ordering1, List<SortOrder> ordering2) {
        if (ordering2.isEmpty()) {
            return true;
        } else if (ordering2.size() > ordering1.size()) {
            return false;
        } else {
            for (int i = 0; i < ordering2.size(); i++) {
                if (!ordering1.get(i).satisfies(ordering2.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public enum SortDirection {
        ASCENDING("ASC", NullOrdering.NULLS_FIRST),
        DESCENDING("DESC", NullOrdering.NULLS_LAST);

        private final String sql;
        private final NullOrdering defaultNullOrdering;

        SortDirection(String sql, NullOrdering defaultNullOrdering) {
            this.sql = sql;
            this.defaultNullOrdering = defaultNullOrdering;
        }

        public String sql() {
            return sql;
        }

        public NullOrdering defaultNullOrdering() {
            return defaultNullOrdering;
        }
    }

    public enum NullOrdering {
        NULLS_FIRST("NULLS FIRST"),
        NULLS_LAST("NULLS LAST");

        private final String sql;

        NullOrdering(String sql) {
            this.sql = sql;
        }

        public String sql() {
            return sql;
        }
    }
}
