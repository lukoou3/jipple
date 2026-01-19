package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.sql.types.DataType;

public abstract class RuntimeReplaceable extends Expression {
    public abstract Expression replacement();

    @Override
    public boolean nullable() {
        return replacement().nullable();
    }

    @Override
    public DataType dataType() {
        return replacement().dataType();
    }

    @Override
    public final Object eval(InternalRow input) {
        throw QueryExecutionErrors.cannotEvaluateExpressionError(this);
    }

}
