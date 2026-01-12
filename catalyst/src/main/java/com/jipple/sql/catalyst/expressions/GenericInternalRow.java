package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.MapData;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.Decimal;
import com.jipple.unsafe.types.CalendarInterval;
import com.jipple.unsafe.types.UTF8String;

public class GenericInternalRow extends InternalRow {
    private final Object[] values;

    public GenericInternalRow(int size) {
        this.values = new Object[size];
    }

    public GenericInternalRow(Object[] values) {
        this.values = values;
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

    @Override
    public InternalRow copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNullAt(int ordinal) {
        return values[ordinal] == null;
    }

    @Override
    public boolean getBoolean(int ordinal) {
        return (boolean) values[ordinal];
    }

    @Override
    public byte getByte(int ordinal) {
        return (byte) values[ordinal];
    }

    @Override
    public short getShort(int ordinal) {
        return (short) values[ordinal];
    }

    @Override
    public int getInt(int ordinal) {
        return (int) values[ordinal];
    }

    @Override
    public long getLong(int ordinal) {
        return (long) values[ordinal];
    }

    @Override
    public float getFloat(int ordinal) {
        return (float) values[ordinal];
    }

    @Override
    public double getDouble(int ordinal) {
        return (double) values[ordinal];
    }

    @Override
    public Decimal getDecimal(int ordinal, int precision, int scale) {
        return (Decimal) values[ordinal];
    }

    @Override
    public UTF8String getUTF8String(int ordinal) {
        return (UTF8String) values[ordinal];
    }

    @Override
    public byte[] getBinary(int ordinal) {
        return (byte[]) values[ordinal];
    }

    @Override
    public CalendarInterval getInterval(int ordinal) {
        return (CalendarInterval) values[ordinal];
    }

    @Override
    public InternalRow getStruct(int ordinal, int numFields) {
        return (InternalRow) values[ordinal];
    }

    @Override
    public ArrayData getArray(int ordinal) {
        return (ArrayData) values[ordinal];
    }

    @Override
    public MapData getMap(int ordinal) {
        return (MapData) values[ordinal];
    }

    @Override
    public Object get(int ordinal, DataType dataType) {
        return values[ordinal];
    }

    @Override
    public Object getObject(int ordinal) {
        return values[ordinal];
    }
}
