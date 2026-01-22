package com.jipple.sql.catalyst.types;

import java.util.Comparator;

public class PhysicalDoubleType extends PhysicalDataType<Double> implements PhysicalPrimitiveType {
    @Override
    public Comparator<Double> comparator() {
        return Comparator.naturalOrder();
    }

    @Override
    public Class<Double> internalTypeClass() {
        return Double.class;
    }
}
