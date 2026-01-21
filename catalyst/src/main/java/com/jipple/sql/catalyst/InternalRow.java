package com.jipple.sql.catalyst;

import com.jipple.sql.catalyst.expressions.GenericInternalRow;
import com.jipple.sql.catalyst.expressions.SpecializedGetters;
import com.jipple.sql.catalyst.types.PhysicalArrayType;
import com.jipple.sql.catalyst.types.PhysicalBinaryType;
import com.jipple.sql.catalyst.types.PhysicalBooleanType;
import com.jipple.sql.catalyst.types.PhysicalCalendarIntervalType;
import com.jipple.sql.catalyst.types.PhysicalDataType;
import com.jipple.sql.catalyst.types.PhysicalDecimalType;
import com.jipple.sql.catalyst.types.PhysicalDoubleType;
import com.jipple.sql.catalyst.types.PhysicalFloatType;
import com.jipple.sql.catalyst.types.PhysicalIntegerType;
import com.jipple.sql.catalyst.types.PhysicalLongType;
import com.jipple.sql.catalyst.types.PhysicalMapType;
import com.jipple.sql.catalyst.types.PhysicalStringType;
import com.jipple.sql.catalyst.types.PhysicalStructType;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.MapData;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.CalendarInterval;
import com.jipple.unsafe.types.UTF8String;

import java.io.Serializable;
import java.util.List;

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


    public static final InternalRow EMPTY = new GenericInternalRow(new Object[0]);

    /**
     * This method can be used to construct a [[InternalRow]] with the given values.
     */
    public static InternalRow of(Object... values) {
        return new GenericInternalRow(values);
    }

    /**
     * This method can be used to construct a [[InternalRow]] from a [[Seq]] of values.
     */
    public static InternalRow fromList(List<Object> values) {
        return new GenericInternalRow(values.toArray());
    }

    /** Returns an empty [[InternalRow]]. */
    public static InternalRow empty() {
        return EMPTY;
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

    public static InternalRowAccessor getAccessor(DataType dataType) {
        return getAccessor(dataType, true);
    }

    /**
     * Returns an accessor for an {@link InternalRow} with given data type.
     * The returned accessor takes a {@link SpecializedGetters} input so it can be reused
     * for other implementations (e.g. {@link ArrayData}).
     */
    public static InternalRowAccessor getAccessor(DataType dataType, boolean nullable) {
        InternalRowAccessor getValueNullSafe;
        PhysicalDataType<?> physicalType = PhysicalDataType.of(dataType);
        if (physicalType instanceof PhysicalBooleanType) {
            getValueNullSafe = (input, ordinal) -> input.getBoolean(ordinal);
        } else if (physicalType instanceof PhysicalIntegerType) {
            getValueNullSafe = (input, ordinal) -> input.getInt(ordinal);
        } else if (physicalType instanceof PhysicalLongType) {
            getValueNullSafe = (input, ordinal) -> input.getLong(ordinal);
        } else if (physicalType instanceof PhysicalFloatType) {
            getValueNullSafe = (input, ordinal) -> input.getFloat(ordinal);
        } else if (physicalType instanceof PhysicalDoubleType) {
            getValueNullSafe = (input, ordinal) -> input.getDouble(ordinal);
        } else if (physicalType instanceof PhysicalStringType) {
            getValueNullSafe = (input, ordinal) -> input.getUTF8String(ordinal);
        } else if (physicalType instanceof PhysicalBinaryType) {
            getValueNullSafe = (input, ordinal) -> input.getBinary(ordinal);
        } else if (physicalType instanceof PhysicalCalendarIntervalType) {
            getValueNullSafe = (input, ordinal) -> input.getInterval(ordinal);
        } else if (physicalType instanceof PhysicalDecimalType decimalType) {
            getValueNullSafe = (input, ordinal) -> input.getDecimal(ordinal, decimalType.precision, decimalType.scale);
        } else if (physicalType instanceof PhysicalStructType structType) {
            getValueNullSafe = (input, ordinal) -> input.getStruct(ordinal, structType.fields.length);
        } else if (physicalType instanceof PhysicalArrayType) {
            getValueNullSafe = (input, ordinal) -> input.getArray(ordinal);
        } else if (physicalType instanceof PhysicalMapType) {
            getValueNullSafe = (input, ordinal) -> input.getMap(ordinal);
        } else {
            getValueNullSafe = (input, ordinal) -> input.get(ordinal, dataType);
        }

        if (nullable) {
            return (getter, index) -> getter.isNullAt(index) ? null : getValueNullSafe.get(getter, index);
        }
        return getValueNullSafe;
    }

}
