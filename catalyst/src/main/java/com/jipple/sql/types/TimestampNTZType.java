package com.jipple.sql.types;

public class TimestampNTZType extends DatetimeType {

    @Override
    public int defaultSize() {
        return 8;
    }

    @Override
    public String typeName() {
        return "timestamp_ntz";
    }

    @Override
    public DataType asNullable() {
        return this;
    }

}
