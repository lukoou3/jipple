package com.jipple.sql.catalyst.types;

import java.util.Comparator;

public class PhysicalLongType extends PhysicalDataType<Long> implements PhysicalPrimitiveType {
    @Override
    public Comparator<Long> comparator() {
        return Comparator.naturalOrder();
    }

    @Override
    public Class<Long> internalTypeClass() {
        return Long.class;
    }
}
