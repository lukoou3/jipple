package com.jipple.sql.catalyst.types;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.types.StructField;

import java.util.Comparator;

public class PhysicalStructType extends PhysicalDataType<InternalRow> {
    public final StructField[] fields;

    public PhysicalStructType(StructField[] fields) {
        this.fields = fields;
    }

    @Override
    public Comparator<InternalRow> comparator() {
        return null;
    }

    @Override
    public Class<InternalRow> internalTypeClass() {
        return InternalRow.class;
    }
}
