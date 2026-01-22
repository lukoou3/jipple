package com.jipple.sql.catalyst.types;

import java.util.Comparator;

public class PhysicalBooleanType extends PhysicalDataType<Boolean> implements PhysicalPrimitiveType {
    @Override
    public Comparator<Boolean> comparator() {
        return Comparator.naturalOrder();
    }

    @Override
    public Class<Boolean> internalTypeClass() {
        return Boolean.class;
    }
}
