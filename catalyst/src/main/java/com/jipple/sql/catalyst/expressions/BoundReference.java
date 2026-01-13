package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.types.DataType;

public class BoundReference extends LeafExpression {
    public final int ordinal;
    public final DataType dataType;
    public final boolean nullable;

    public BoundReference(int ordinal, DataType dataType) {
        this(ordinal, dataType, true);
    }

    public BoundReference(int ordinal, DataType dataType, boolean nullable) {
        this.ordinal = ordinal;
        this.dataType = dataType;
        this.nullable = nullable;
    }

    @Override
    public String toString() {
        return "input[" + ordinal + ", " + dataType.simpleString() + ", " + nullable + "]";
    }

    @Override
    public Object[] args() {
        return new Object[]{ordinal, dataType, nullable};
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

    @Override
    public DataType dataType() {
        return dataType;
    }

    @Override
    public Object eval(InternalRow input) {
        return input.get(ordinal, dataType);
    }

}
