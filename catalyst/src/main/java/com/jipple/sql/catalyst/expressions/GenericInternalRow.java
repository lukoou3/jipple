package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.types.DataType;

import java.util.List;

/**
 * An internal row implementation that uses an array of objects as the underlying storage.
 * Note that, while the array is not copied, and thus could technically be mutated after creation,
 * this is not allowed.
 */
public class GenericInternalRow extends BaseGenericInternalRow {
    private final Object[] values;

    /** No-arg constructor for serialization. */
    protected GenericInternalRow() {
        this.values = null;
    }

    public GenericInternalRow(int size) {
        this.values = new Object[size];
    }

    public GenericInternalRow(Object[] values) {
        this.values = values;
    }

    @Override
    protected Object genericGet(int ordinal) {
        return values[ordinal];
    }

    @Override
    public int numFields() {
        return values.length;
    }

    @Override
    public void setNullAt(int i) {
        values[i] = null;
    }

    @Override
    public void update(int i, Object value) {
        values[i] = value;
    }
}
