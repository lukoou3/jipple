package com.jipple.sql.catalyst.expressions.string;

import com.jipple.sql.catalyst.expressions.BinaryExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;
import java.util.Optional;

import static com.jipple.sql.types.DataTypes.*;

public class StringRepeat extends BinaryExpression {
    public StringRepeat(Expression str, Expression times) {
        super(str, times);
    }

    @Override
    public Optional<List<AbstractDataType>> expectsInputTypes() {
        return Optional.of(List.of(STRING, INTEGER));
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    protected Object nullSafeEval(Object string, Object n) {
        return ((UTF8String)string).repeat((Integer)n);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new StringRepeat(newLeft, newRight);
    }

}
