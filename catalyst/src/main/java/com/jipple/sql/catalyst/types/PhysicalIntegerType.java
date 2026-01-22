package com.jipple.sql.catalyst.types;

import java.util.Comparator;

public class PhysicalIntegerType extends PhysicalDataType<Integer> implements PhysicalPrimitiveType {
    @Override
    public Comparator<Integer> comparator() {
        return Comparator.naturalOrder();
    }

    @Override
    public Class<Integer> internalTypeClass() {
        return Integer.class;
    }
}
