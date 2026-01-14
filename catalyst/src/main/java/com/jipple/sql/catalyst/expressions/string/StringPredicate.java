package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.BinaryExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

import static com.jipple.sql.types.DataTypes.*;

public abstract class StringPredicate extends BinaryExpression {
    public StringPredicate(Expression left, Expression right) {
        super(left, right);
    }

    public abstract boolean compare(UTF8String l, UTF8String r);

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.of(List.of(STRING, STRING));
    }

    @Override
    protected Object nullSafeEval(Object input1, Object input2) {
        return compare((UTF8String) input1, (UTF8String) input2);
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    @Override
    public String toString() {
        return String.format("%s(%s, %s)", nodeName(), left, right);
    }

}
