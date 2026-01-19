package com.jipple.sql.catalyst.expressions.nvl;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.ComplexTypeMergingExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.util.TypeUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Coalesce extends ComplexTypeMergingExpression {
    private final List<Expression> children;
    public Coalesce(List<Expression> children) {
        this.children = children;
    }

    @Override
    public Object[] args() {
        return new Object[]{children};
    }

    @Override
    public List<Expression> children() {
        return children;
    }

    @Override
    public boolean nullable() {
        return children.stream().allMatch(Expression::nullable);
    }

    @Override
    public boolean foldable() {
        return children.stream().allMatch(Expression::foldable);
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        if (children.isEmpty()) {
            return TypeCheckResult.typeCheckFailure(prettyName() +  "requires at least one argument");
        } else {
            return TypeUtils.checkForSameTypeInputExpr(children().stream().map(Expression::dataType).collect(Collectors.toList()), prettyName());
        }
    }

    @Override
    public Object eval(InternalRow input) {
        List<Expression> children = children();
        for (int i = 0; i < children.size(); i++) {
            Object result = children.get(i).eval(input);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new Coalesce(newChildren);
    }
}
