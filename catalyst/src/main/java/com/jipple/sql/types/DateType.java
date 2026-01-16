package com.jipple.sql.types;

public class DateType extends DatetimeType {
    public static final DateType INSTANCE = new DateType();
    private DateType() {}
    @Override
    public int defaultSize() {
        return 4;
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
