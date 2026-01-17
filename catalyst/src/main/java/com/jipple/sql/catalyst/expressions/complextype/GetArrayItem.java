package com.jipple.sql.catalyst.expressions.complextype;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.BinaryExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.*;

import java.util.List;

import static com.jipple.sql.types.DataTypes.ANY;
import static com.jipple.sql.types.DataTypes.INTEGER;

public class GetArrayItem extends BinaryExpression {
    public GetArrayItem(Expression child, Expression ordinal) {
        super(child, ordinal);
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        // We have done type checking for child in `ExtractValue`, so only need to check the `ordinal`.
        return Option.some(List.of(ANY, INTEGER));
    }

    @Override
    public DataType dataType() {
        return ((ArrayType)left.dataType()).elementType;
    }

    @Override
    protected Object nullSafeEval(Object value, Object ordinal) {
        return null;
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new GetArrayItem(newLeft, newRight);
    }
}
