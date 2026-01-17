package com.jipple.sql.catalyst.expressions.complextype;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.types.DataType;

public class GetStructField extends UnaryExpression {
    public final int ordinal;
    public final Option<String> name;

    public GetStructField(Expression child, int ordinal) {
        this(child, ordinal, Option.empty());
    }

    public GetStructField(Expression child, int ordinal, Option<String> name) {
        super(child);
        this.ordinal = ordinal;
        this.name = name;
    }

    @Override
    public Object[] args() {
        return new Object[]{child, ordinal, name};
    }

    @Override
    public DataType dataType() {
        return null;
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new GetStructField(newChild, ordinal, name);
    }
}
