package com.jipple.sql.catalyst.types;

import com.jipple.sql.types.Decimal;

import java.util.Comparator;

public class PhysicalDecimalType extends PhysicalDataType<Decimal> {
    public final int precision;
    public final int scale;

    PhysicalDecimalType(int precision, int scale) {
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public Comparator<Decimal> comparator() {
        return Comparator.naturalOrder();
    }

    @Override
    public Class<Decimal> internalTypeClass() {
        return Decimal.class;
    }
}
