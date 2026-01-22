package com.jipple.sql.catalyst.types;

import java.util.Comparator;

public class PhysicalNullType extends PhysicalDataType<Object> implements PhysicalPrimitiveType {
    @Override
    public Comparator<Object> comparator() {
        return (x , y)  -> 0;
    }

    @Override
    public Class<Object> internalTypeClass() {
        return Object.class;
    }
}
