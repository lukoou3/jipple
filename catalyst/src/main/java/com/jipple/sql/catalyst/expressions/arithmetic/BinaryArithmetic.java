package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.sql.catalyst.expressions.BinaryOperator;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.DataType;

public abstract class BinaryArithmetic extends BinaryOperator {
    public BinaryArithmetic(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public DataType dataType() {
        return left.dataType();
    }

}
