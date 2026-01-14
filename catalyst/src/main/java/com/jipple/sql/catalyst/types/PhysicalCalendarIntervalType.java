package com.jipple.sql.catalyst.types;

import java.util.Comparator;

public class PhysicalCalendarIntervalType extends PhysicalDataType<Object> {
    @Override
    public Comparator<Object> comparator() {
        throw new IllegalArgumentException("Type PhysicalCalendarIntervalType does not support ordered operations.");
    }

    @Override
    public Class<Object> internalTypeClass() {
        return Object.class;
    }
}
