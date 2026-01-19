package com.jipple.sql.catalyst.util;

import com.jipple.sql.catalyst.expressions.SpecializedGetters;

import java.io.Serializable;

public abstract class ArrayData implements SpecializedGetters, Serializable {
    public abstract int numElements();

    public abstract ArrayData copy();

    public abstract Object[] array();

    public abstract void setNullAt(int i);

    public abstract void update(int i, Object value);

    // default implementation (slow)
    public void setBoolean(int i, boolean value) {
        update(i, value);
    }

    public void setInt(int i, int value) {
        update(i, value);
    }

    public void setLong(int i, long value) {
        update(i, value);
    }

    public void setFloat(int i, float value) {
        update(i, value);
    }

    public void setDouble(int i, double value) {
        update(i, value);
    }

    public boolean[] toBooleanArray() {
        int size = numElements();
        boolean[] values = new boolean[size];
        for (int i = 0; i < size; i++) {
            values[i] = getBoolean(i);
        }
        return values;
    }

    public int[] toIntArray() {
        int size = numElements();
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = getInt(i);
        }
        return values;
    }

    public long[] toLongArray() {
        int size = numElements();
        long[] values = new long[size];
        for (int i = 0; i < size; i++) {
            values[i] = getLong(i);
        }
        return values;
    }

    public float[] toFloatArray() {
        int size = numElements();
        float[] values = new float[size];
        for (int i = 0; i < size; i++) {
            values[i] = getFloat(i);
        }
        return values;
    }

    public double[] toDoubleArray() {
        int size = numElements();
        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = getDouble(i);
        }
        return values;
    }

}
