package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.TernaryExpression;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

import static com.jipple.sql.types.DataTypes.*;

public class Substring extends TernaryExpression {
    protected Substring(Expression str, Expression pos, Expression len) {
        super(str, pos, len);
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.of(List.of(STRING, INTEGER, INTEGER));
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    protected Object nullSafeEval(Object string, Object pos, Object len) {
        return ((UTF8String) string).substringSQL((Integer) pos, (Integer) len);
    }

    @Override
    public Expression withNewChildInternal(Expression newFirst, Expression newSecond, Expression newThird) {
        return new Substring(newFirst, newSecond, newThird);
    }

}
