package com.jipple.sql.types;

public class FloatType extends FractionalType {
    public static final FloatType INSTANCE = new FloatType();
    private FloatType() {
    }

    @Override
    public int defaultSize() {
        return 4;
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
