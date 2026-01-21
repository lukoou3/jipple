package com.jipple.sql.catalyst.types;

import com.jipple.sql.catalyst.util.MapData;
import com.jipple.sql.types.DataType;

import java.util.Comparator;

public class PhysicalMapType extends PhysicalDataType<MapData> {
    public final DataType keyType;
    public final DataType valueType;
    public final boolean valueContainsNull;

    public PhysicalMapType(DataType keyType, DataType valueType, boolean valueContainsNull) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.valueContainsNull = valueContainsNull;
    }

    @Override
    public Comparator<MapData> comparator() {
        throw new IllegalArgumentException("Type PhysicalMapType does not support ordered operations.");
    }

    @Override
    public Class<MapData> internalTypeClass() {
        return MapData.class;
    }
}
