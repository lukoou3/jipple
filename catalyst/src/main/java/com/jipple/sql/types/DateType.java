package com.jipple.sql.types;

public class DateType extends DatetimeType {
    @Override
    public int defaultSize() {
        return 4;
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
