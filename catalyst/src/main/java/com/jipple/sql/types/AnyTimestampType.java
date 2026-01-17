package com.jipple.sql.types;

public class AnyTimestampType extends AbstractDataType {
    @Override
    public DataType defaultConcreteType() {
        return TimestampType.INSTANCE;
    }

    @Override
    public boolean acceptsType(DataType other) {
        return other instanceof TimestampType || other instanceof TimestampNTZType;
    }

    @Override
    public String simpleString() {
        return "(timestamp or timestamp without time zone)";
    }
}
