package com.jipple.sql.catalyst.types;

import java.util.Comparator;

public class UninitializedPhysicalType extends PhysicalDataType<Object> {
    @Override
    public Comparator<Object> comparator() {
        throw new IllegalArgumentException("Type UninitializedPhysicalType does not support ordered operations.");
    }

    @Override
    public Class<Object> internalTypeClass() {
        return Object.class;
    }
}