package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StringType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

public abstract class String2StringExpression extends UnaryExpression {
    public String2StringExpression(Expression child) {
        super(child);
    }

    protected abstract UTF8String convert(UTF8String value);

    @Override
    public DataType dataType() {
        return StringType.INSTANCE;
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(StringType.INSTANCE));
    }

    @Override
    protected Object nullSafeEval(Object input) {
        return convert((UTF8String) input);
    }
}
