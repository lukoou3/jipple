package com.jipple.sql.types;

public class DoubleType extends FractionalType {
    public static final DoubleType INSTANCE = new DoubleType();
    private DoubleType() {
    }

    @Override
    public int defaultSize() {
        return 8;
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
