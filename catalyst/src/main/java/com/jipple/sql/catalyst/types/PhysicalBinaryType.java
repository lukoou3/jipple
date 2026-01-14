package com.jipple.sql.catalyst.types;

import com.jipple.unsafe.types.ByteArray;

import java.util.Comparator;

public class PhysicalBinaryType extends PhysicalDataType<byte[]> {
    @Override
    public Comparator<byte[]> comparator() {
        return (x, y) -> ByteArray.compareBinary(x, y);
    }

    @Override
    public Class<byte[]> internalTypeClass() {
        return byte[].class;
    }
}
