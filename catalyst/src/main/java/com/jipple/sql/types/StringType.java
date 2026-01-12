package com.jipple.sql.types;

public class StringType extends AtomicType {
    public static final StringType INSTANCE = new StringType();
    private StringType() {
    }

    @Override
    public int defaultSize() {
        return 20;
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
