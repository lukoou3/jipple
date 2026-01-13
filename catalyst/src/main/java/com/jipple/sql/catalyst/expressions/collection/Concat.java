package com.jipple.sql.catalyst.expressions.collection;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

import static com.jipple.sql.types.DataTypes.STRING;

public class Concat extends Expression {
    private final List<Expression> children;
    private final UTF8String[] inputs;
    public Concat(List<Expression> children) {
        this.children = children;
        this.inputs = new UTF8String[children.size()];
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
        return children().stream().anyMatch(Expression::nullable);
    }

    @Override
    public DataType dataType() {
        return children.isEmpty() ? STRING : children.get(0).dataType();
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        if (children.stream().anyMatch(child -> !child.dataType().equals(STRING))) {
            return TypeCheckResult.typeCheckFailure("requires all arguments to be string type");
        }
        return super.checkInputDataTypes();
    }

    @Override
    public Object eval(InternalRow input) {
        for (int i = 0; i < children.size(); i++) {
            inputs[i] = (UTF8String) children.get(i).eval(input);
        }
        return UTF8String.concat(inputs);
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new Concat(newChildren);
    }
}
