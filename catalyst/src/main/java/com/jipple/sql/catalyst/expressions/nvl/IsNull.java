package com.jipple.sql.catalyst.expressions.nvl;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.types.DataType;

public class IsNull extends UnaryExpression {

    public IsNull(Expression child) {
        super(child);
    }

    @Override
    public DataType dataType() {
        return null;
    }

    @Override
    public Object eval(InternalRow input) {
        return child.eval(input) == null;
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new IsNull(newChild);
    }
}
