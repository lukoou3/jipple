package com.jipple.sql.types;

public class TimestampNTZType extends DatetimeType {
    public static final TimestampNTZType INSTANCE = new TimestampNTZType();
    private TimestampNTZType() {}
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
