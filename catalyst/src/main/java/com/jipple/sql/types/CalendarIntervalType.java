package com.jipple.sql.types;

public class CalendarIntervalType extends DataType {
    @Override
    public int defaultSize() {
        return 16;
    }

    @Override
    public String typeName() {
        return "interval";
    }

    @Override
    public DataType asNullable() {
        return this;
    }
}
