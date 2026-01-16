package com.jipple.sql.types;

public class TimestampType extends DatetimeType {
    public static final TimestampType INSTANCE = new TimestampType();
    private TimestampType() {}
    @Override
    public int defaultSize() {
        return 8;
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
