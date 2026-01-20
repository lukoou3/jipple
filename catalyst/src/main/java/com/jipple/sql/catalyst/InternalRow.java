package com.jipple.sql.catalyst;

import com.jipple.sql.catalyst.expressions.SpecializedGetters;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.MapData;
import com.jipple.unsafe.types.CalendarInterval;
import com.jipple.unsafe.types.UTF8String;

import java.io.Serializable;

public abstract class InternalRow implements SpecializedGetters, Serializable {

    public abstract int numFields();

    // This is only use for test and will throw a null pointer exception if the position is null.
    public String getString(int ordinal) {
        return getUTF8String(ordinal).toString();
    }

    public abstract void setNullAt(int i);

    /**
     * Updates the value at column `i`. Note that after updating, the given value will be kept in this
     * row, and the caller side should guarantee that this value won't be changed afterwards.
     */
    public abstract void update(int i, Object value);

    // default implementation (slow)
    public void setBoolean(int i, boolean value) {
        update(i, value);
    }

    public void setByte(int i, byte value) {
        update(i, value);
    }

    public void setShort(int i, short value) {
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

    /**
     * Update the decimal column at `i`.
     *
     * Note: In order to support update decimal with precision > 18 in UnsafeRow,
     * CAN NOT call setNullAt() for decimal column on UnsafeRow, call setDecimal(i, null, precision).
     */
    //public void setDecimal(int i, value: Decimal, precision: Int): Unit = update(i, value)

    public void setInterval(int i, CalendarInterval value) {
        update(i, value);
    }

    /**
     * Make a copy of the current [[InternalRow]] object.
     */
    public abstract InternalRow copy();

    /** Returns true if there are any NULL values in this row. */
    public boolean anyNull() {
        int len = numFields();
        for (int i = 0; i < len; i++) {
            if (isNullAt(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Copies the given value if it's string/struct/array/map type.
     */
    public static Object copyValue(Object value) {
        if (value instanceof UTF8String) {
            return ((UTF8String) value).copy();
        } else if (value instanceof InternalRow) {
            return ((InternalRow) value).copy();
        } else if (value instanceof ArrayData) {
            return ((ArrayData) value).copy();
        } else if (value instanceof MapData) {
            return ((MapData) value).copy();
        } else {
            return value;
        }
    }
}
