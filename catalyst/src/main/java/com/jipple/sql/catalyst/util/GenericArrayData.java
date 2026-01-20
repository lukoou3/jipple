package com.jipple.sql.catalyst.util;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.Decimal;
import com.jipple.unsafe.types.CalendarInterval;
import com.jipple.unsafe.types.UTF8String;

import java.util.Arrays;
import java.util.List;

/**
 * A generic ArrayData implementation that uses an Object array to store the elements.
 */
public class GenericArrayData extends ArrayData {
    private final Object[] array;

    /**
     * Creates a GenericArrayData from an Object array.
     */
    public GenericArrayData(Object[] array) {
        this.array = array;
    }

    /**
     * Creates a GenericArrayData from a List.
     */
    public GenericArrayData(List<Object> list) {
        this.array = list.toArray(new Object[list.size()]);
    }

    /**
     * Creates a GenericArrayData from primitive arrays (boxing them).
     */
    public GenericArrayData(int[] primitiveArray) {
        this.array = new Object[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++) {
            this.array[i] = primitiveArray[i];
        }
    }

    public GenericArrayData(long[] primitiveArray) {
        this.array = new Object[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++) {
            this.array[i] = primitiveArray[i];
        }
    }

    public GenericArrayData(float[] primitiveArray) {
        this.array = new Object[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++) {
            this.array[i] = primitiveArray[i];
        }
    }

    public GenericArrayData(double[] primitiveArray) {
        this.array = new Object[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++) {
            this.array[i] = primitiveArray[i];
        }
    }

    public GenericArrayData(boolean[] primitiveArray) {
        this.array = new Object[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++) {
            this.array[i] = primitiveArray[i];
        }
    }

    @Override
    public ArrayData copy() {
        Object[] newValues = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            Object value = array[i];
            newValues[i] = InternalRow.copyValue(value);
        }
        return new GenericArrayData(newValues);
    }

    @Override
    public int numElements() {
        return array.length;
    }

    @SuppressWarnings("unchecked")
    private <T> T getAs(int ordinal) {
        return (T) array[ordinal];
    }

    @Override
    public boolean isNullAt(int ordinal) {
        return array[ordinal] == null;
    }

    @Override
    public Object get(int ordinal, DataType elementType) {
        return getAs(ordinal);
    }

    @Override
    public Object getObject(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public boolean getBoolean(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public byte getByte(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public short getShort(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public int getInt(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public long getLong(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public float getFloat(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public double getDouble(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public Decimal getDecimal(int ordinal, int precision, int scale) {
        return getAs(ordinal);
    }

    @Override
    public UTF8String getUTF8String(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public byte[] getBinary(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public CalendarInterval getInterval(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public InternalRow getStruct(int ordinal, int numFields) {
        return getAs(ordinal);
    }

    @Override
    public ArrayData getArray(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public MapData getMap(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public void setNullAt(int ordinal) {
        array[ordinal] = null;
    }

    @Override
    public void update(int ordinal, Object value) {
        array[ordinal] = value;
    }

    @Override
    public Object[] array() {
        return array;
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GenericArrayData)) {
            return false;
        }

        GenericArrayData other = (GenericArrayData) o;
        if (other == null) {
            return false;
        }

        int len = numElements();
        if (len != other.numElements()) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (isNullAt(i) != other.isNullAt(i)) {
                return false;
            }
            if (!isNullAt(i)) {
                Object o1 = array[i];
                Object o2 = other.array[i];
                
                if (o1 instanceof byte[]) {
                    if (!(o2 instanceof byte[]) || !Arrays.equals((byte[]) o1, (byte[]) o2)) {
                        return false;
                    }
                } else if (o1 instanceof Float && Float.isNaN((Float) o1)) {
                    if (!(o2 instanceof Float) || !Float.isNaN((Float) o2)) {
                        return false;
                    }
                } else if (o1 instanceof Double && Double.isNaN((Double) o1)) {
                    if (!(o2 instanceof Double) || !Double.isNaN((Double) o2)) {
                        return false;
                    }
                } else {
                    if (o1.getClass() != o2.getClass() || !o1.equals(o2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 37;
        int len = numElements();
        for (int i = 0; i < len; i++) {
            int update;
            if (isNullAt(i)) {
                update = 0;
            } else {
                Object value = array[i];
                if (value instanceof Boolean) {
                    update = ((Boolean) value) ? 0 : 1;
                } else if (value instanceof Byte) {
                    update = ((Byte) value).intValue();
                } else if (value instanceof Short) {
                    update = ((Short) value).intValue();
                } else if (value instanceof Integer) {
                    update = (Integer) value;
                } else if (value instanceof Long) {
                    long l = (Long) value;
                    update = (int) (l ^ (l >>> 32));
                } else if (value instanceof Float) {
                    update = Float.floatToIntBits((Float) value);
                } else if (value instanceof Double) {
                    long b = Double.doubleToLongBits((Double) value);
                    update = (int) (b ^ (b >>> 32));
                } else if (value instanceof byte[]) {
                    update = Arrays.hashCode((byte[]) value);
                } else {
                    update = value.hashCode();
                }
            }
            result = 37 * result + update;
        }
        return result;
    }
}

