package com.jipple.sql.catalyst.expressions.nvl;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.types.DataType;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class IsNotNull extends UnaryExpression {

    public IsNotNull(Expression child) {
        super(child);
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    @Override
    public Object eval(InternalRow input) {
        return child.eval(input) != null;
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new IsNotNull(newChild);
    }
}
