package com.jipple.sql.types;

public class IntegerType extends IntegralType {
    public static final IntegerType INSTANCE = new IntegerType();
    IntegerType() {
    }

    @Override
    public int defaultSize() {
        return 4;
    }

    @Override
    public String simpleString() {
        return "int";
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
