package com.jipple.sql.catalyst.expressions.objects;

import com.google.common.base.Preconditions;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.InternalRowAccessor;
import com.jipple.sql.catalyst.expressions.LeafExpression;
import com.jipple.sql.types.DataType;

/**
 * A placeholder for the loop variable used in [[MapObjects]]. This should never be constructed
 * manually, but will instead be passed into the provided lambda function.
 */
// TODO: Merge this and `NamedLambdaVariable`.
public class LambdaVariable extends LeafExpression {
    public final String name;
    public final DataType dataType;
    public final boolean nullable;
    public final long id;
    private final InternalRowAccessor accessor;

    public LambdaVariable(String name, DataType dataType, boolean nullable, long id) {
        this.name = name;
        this.dataType = dataType;
        this.nullable = nullable;
        this.id = id;
        this.accessor = InternalRow.getAccessor(dataType, nullable);
    }

    @Override
    public Object[] args() {
        return new Object[] {name, dataType, nullable, id};
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

    @Override
    public DataType dataType() {
        return dataType;
    }

    // Interpreted execution of `LambdaVariable` always get the 0-index element from input row.
    @Override
    public Object eval(InternalRow input) {
        Preconditions.checkArgument(input.numFields() == 1, "The input row of interpreted LambdaVariable should have only 1 field.");
        return accessor.get(input, 0);
    }
}
