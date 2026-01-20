package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.MapData;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.Decimal;
import com.jipple.unsafe.types.CalendarInterval;
import com.jipple.unsafe.types.UTF8String;

import java.util.Arrays;

/**
 * An extended version of [[InternalRow]] that implements all special getters, toString
 * and equals/hashCode by `genericGet`.
 */
public abstract class BaseGenericInternalRow extends InternalRow {

    /**
     * Gets the value at the given ordinal position.
     * This is the core method that all other getters delegate to.
     */
    protected abstract Object genericGet(int ordinal);

    // default implementation (slow)
    @SuppressWarnings("unchecked")
    private <T> T getAs(int ordinal) {
        return (T) genericGet(ordinal);
    }

    @Override
    public boolean isNullAt(int ordinal) {
        Object value = genericGet(ordinal);
        return value == null;
    }

    @Override
    public Object get(int ordinal, DataType dataType) {
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
    public ArrayData getArray(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public CalendarInterval getInterval(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public MapData getMap(int ordinal) {
        return getAs(ordinal);
    }

    @Override
    public InternalRow getStruct(int ordinal, int numFields) {
        return getAs(ordinal);
    }

    @Override
    public String toString() {
        int numFields = numFields();
        if (numFields == 0) {
            return "[empty row]";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(genericGet(0));
            int len = numFields;
            for (int i = 1; i < len; i++) {
                sb.append(",");
                sb.append(genericGet(i));
            }
            sb.append("]");
            return sb.toString();
        }
    }

    @Override
    public InternalRow copy() {
        int len = numFields();
        Object[] newValues = new Object[len];
        for (int i = 0; i < len; i++) {
            newValues[i] = InternalRow.copyValue(genericGet(i));
        }
        return new GenericInternalRow(newValues);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BaseGenericInternalRow)) {
            return false;
        }

        BaseGenericInternalRow other = (BaseGenericInternalRow) o;

        int len = numFields();
        if (len != other.numFields()) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (isNullAt(i) != other.isNullAt(i)) {
                return false;
            }
            if (!isNullAt(i)) {
                Object o1 = genericGet(i);
                Object o2 = other.genericGet(i);
                if (o1 instanceof byte[]) {
                    if (!(o2 instanceof byte[]) ||
                            !Arrays.equals((byte[]) o1, (byte[]) o2)) {
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
                    if (!o1.equals(o2)) {
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
        int len = numFields();
        for (int i = 0; i < len; i++) {
            int update;
            if (isNullAt(i)) {
                update = 0;
            } else {
                Object value = genericGet(i);
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

