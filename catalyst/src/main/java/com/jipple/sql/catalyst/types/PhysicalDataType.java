package com.jipple.sql.catalyst.types;

import com.jipple.sql.types.DataType;
import com.jipple.sql.types.IntegerType;

import java.util.Comparator;


public abstract class PhysicalDataType<T> {
    public abstract Comparator<T> comparator();
    public abstract Class<T> internalTypeClass();

    public static PhysicalDataType<?> of (DataType dataType) {
        if (dataType instanceof IntegerType) {
            return new PhysicalIntegerType();
        } else {
            return null;
        }
    }
}

