package com.jipple.sql.catalyst.types;

import java.util.Comparator;

public class PhysicalFloatType extends PhysicalDataType<Float>  {
    @Override
    public Comparator<Float> comparator() {
        return Comparator.naturalOrder();
    }

    @Override
    public Class<Float> internalTypeClass() {
        return Float.class;
    }
}
