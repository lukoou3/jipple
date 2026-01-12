package com.jipple.sql.types;

public class TimestampType extends DatetimeType {
    @Override
    public int defaultSize() {
        return 8;
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
