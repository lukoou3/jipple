package com.jipple.sql.types;

public class BooleanType extends  AtomicType {
    public static final BooleanType INSTANCE = new BooleanType();
    private BooleanType() {
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
