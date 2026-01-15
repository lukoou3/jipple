package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.types.*;

import java.util.List;
import java.util.function.Function;

import static com.jipple.sql.types.DataTypes.NUMERIC;

public class UnaryMinus extends UnaryExpression {
    Function<Object, Object> negate;
    public UnaryMinus(Expression child) {
        super(child);
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(NUMERIC));
    }

    @Override
    public DataType dataType() {
        return child.dataType();
    }

    @Override
    protected Object nullSafeEval(Object input) {
        if (negate == null) {
            DataType dataType = dataType();
            if (dataType instanceof IntegerType) {
                negate = (x) -> -((Integer) x);
            } else if (dataType instanceof LongType) {
                negate = (x) -> -((Long) x);
            } else if (dataType instanceof DoubleType) {
                negate = (x) -> -((Double) x);
            } else {
                throw new RuntimeException();
            }
        }
        return negate.apply(input);
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new UnaryMinus(newChild);
    }
}
