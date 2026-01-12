package com.jipple.sql.types;

public class LongType extends IntegralType {
    public static final LongType INSTANCE = new LongType();
    private LongType() {
    }

    @Override
    public int defaultSize() {
        return 8;
    }

    @Override
    public String simpleString() {
        return "bigint";
    }

    @Override
    public DataType asNullable() {
        return null;
    }
}
