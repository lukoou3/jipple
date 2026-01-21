package com.jipple.sql;

import java.util.Arrays;
import java.util.List;

/**
 * A row implementation that uses an array of objects as the underlying storage.  Note that, while
 * the array is not copied, and thus could technically be mutated after creation, this is not
 * allowed.
 */
public class GenericRow extends Row {
    protected final Object[] values;

    /** No-arg constructor for serialization. */
    protected GenericRow() {
        this(null);
    }

    public GenericRow(int size) {
        this.values = new Object[size];
    }

    public GenericRow(Object[] values) {
        this.values = values;
    }

    @Override
    public int length() {
        return values.length;
    }

    @Override
    public Object get(int i) {
        return values[i];
    }

    @Override
    public void setNullAt(int i) {
        values[i] = null;
    }

    @Override
    public void update(int i, Object value) {
        values[i] = value;
    }

    @Override
    public List<Object> toList() {
        return Arrays.asList(values);
    }

    @Override
    public Row copy() {
        return this;
    }
}

