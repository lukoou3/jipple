package com.jipple.sql.types;

public class NullType extends DataType {
    public static final NullType INSTANCE = new NullType();
    private NullType() {
    }
    @Override
    public int defaultSize() {
        return 1;
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
