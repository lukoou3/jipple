package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.types.*;

import java.io.Serializable;
import java.util.List;

/**
 * A row type that holds an array specialized container objects, of type [[MutableValue]], chosen
 * based on the dataTypes of each column. The intent is to decrease garbage when modifying the
 * values of primitive columns.
 */
public final class SpecificInternalRow extends BaseGenericInternalRow {
    private final MutableValue[] values;

    private MutableValue dataTypeToMutableValue(DataType dataType) {
        // We use INT for DATE internally
        if (dataType instanceof IntegerType || dataType instanceof DateType) {
            return new MutableInt();
        }
        // We use Long for Timestamp, Timestamp without time zone internally
        if (dataType instanceof LongType || 
            dataType instanceof TimestampType || 
            dataType instanceof TimestampNTZType) {
            return new MutableLong();
        }
        if (dataType instanceof FloatType) {
            return new MutableFloat();
        }
        if (dataType instanceof DoubleType) {
            return new MutableDouble();
        }
        if (dataType instanceof BooleanType) {
            return new MutableBoolean();
        }
        // For all other types, use MutableAny
        return new MutableAny();
    }

    public SpecificInternalRow(List<DataType> dataTypes) {
        this.values = new MutableValue[dataTypes.size()];
        int i = 0;
        for (DataType dt : dataTypes) {
            values[i] = dataTypeToMutableValue(dt);
            i++;
        }
    }

    public SpecificInternalRow() {
        this.values = new MutableValue[0];
    }

    public SpecificInternalRow(StructType schema) {
        this.values = new MutableValue[schema.fields.length];
        int length = values.length;
        StructField[] fields = schema.fields;
        for (int i = 0; i < length; i++) {
            values[i] = dataTypeToMutableValue(fields[i].dataType);
        }
    }

    @Override
    public int numFields() {
        return values.length;
    }

    @Override
    public void setNullAt(int i) {
        values[i].isNull = true;
    }

    @Override
    public boolean isNullAt(int i) {
        return values[i].isNull;
    }

    @Override
    protected Object genericGet(int i) {
        return values[i].boxed();
    }

    @Override
    public void update(int ordinal, Object value) {
        if (value == null) {
            setNullAt(ordinal);
        } else {
            values[ordinal].update(value);
        }
    }

    @Override
    public void setInt(int ordinal, int value) {
        MutableInt currentValue = (MutableInt) values[ordinal];
        currentValue.isNull = false;
        currentValue.value = value;
    }

    @Override
    public int getInt(int ordinal) {
        return ((MutableInt) values[ordinal]).value;
    }

    @Override
    public void setFloat(int ordinal, float value) {
        MutableFloat currentValue = (MutableFloat) values[ordinal];
        currentValue.isNull = false;
        currentValue.value = value;
    }

    @Override
    public float getFloat(int ordinal) {
        return ((MutableFloat) values[ordinal]).value;
    }

    @Override
    public void setBoolean(int ordinal, boolean value) {
        MutableBoolean currentValue = (MutableBoolean) values[ordinal];
        currentValue.isNull = false;
        currentValue.value = value;
    }

    @Override
    public boolean getBoolean(int ordinal) {
        return ((MutableBoolean) values[ordinal]).value;
    }

    @Override
    public void setDouble(int ordinal, double value) {
        MutableDouble currentValue = (MutableDouble) values[ordinal];
        currentValue.isNull = false;
        currentValue.value = value;
    }

    @Override
    public double getDouble(int ordinal) {
        return ((MutableDouble) values[ordinal]).value;
    }

    @Override
    public void setShort(int ordinal, short value) {
        MutableShort currentValue = (MutableShort) values[ordinal];
        currentValue.isNull = false;
        currentValue.value = value;
    }

    @Override
    public short getShort(int ordinal) {
        return ((MutableShort) values[ordinal]).value;
    }

    @Override
    public void setLong(int ordinal, long value) {
        MutableLong currentValue = (MutableLong) values[ordinal];
        currentValue.isNull = false;
        currentValue.value = value;
    }

    @Override
    public long getLong(int ordinal) {
        return ((MutableLong) values[ordinal]).value;
    }

    @Override
    public void setByte(int ordinal, byte value) {
        MutableByte currentValue = (MutableByte) values[ordinal];
        currentValue.isNull = false;
        currentValue.value = value;
    }

    @Override
    public byte getByte(int ordinal) {
        return ((MutableByte) values[ordinal]).value;
    }


    /**
     * A parent class for mutable container objects that are reused when the values are changed,
     * resulting in less garbage. These values are held by a [[SpecificInternalRow]].
     */
    abstract static class MutableValue implements Serializable {
        boolean isNull = true;

        abstract Object boxed();

        abstract void update(Object v);

        abstract MutableValue copy();
    }

    static final class MutableInt extends MutableValue {
        int value = 0;

        @Override
        Object boxed() {
            return isNull ? null : value;
        }

        @Override
        void update(Object v) {
            isNull = false;
            value = (Integer) v;
        }

        @Override
        MutableInt copy() {
            MutableInt newCopy = new MutableInt();
            newCopy.isNull = isNull;
            newCopy.value = value;
            return newCopy;
        }
    }

    static final class MutableFloat extends MutableValue {
        float value = 0;

        @Override
        Object boxed() {
            return isNull ? null : value;
        }

        @Override
        void update(Object v) {
            isNull = false;
            value = (Float) v;
        }

        @Override
        MutableFloat copy() {
            MutableFloat newCopy = new MutableFloat();
            newCopy.isNull = isNull;
            newCopy.value = value;
            return newCopy;
        }
    }

    static final class MutableBoolean extends MutableValue {
        boolean value = false;

        @Override
        Object boxed() {
            return isNull ? null : value;
        }

        @Override
        void update(Object v) {
            isNull = false;
            value = (Boolean) v;
        }

        @Override
        MutableBoolean copy() {
            MutableBoolean newCopy = new MutableBoolean();
            newCopy.isNull = isNull;
            newCopy.value = value;
            return newCopy;
        }
    }

    static final class MutableDouble extends MutableValue {
        double value = 0;

        @Override
        Object boxed() {
            return isNull ? null : value;
        }

        @Override
        void update(Object v) {
            isNull = false;
            value = (Double) v;
        }

        @Override
        MutableDouble copy() {
            MutableDouble newCopy = new MutableDouble();
            newCopy.isNull = isNull;
            newCopy.value = value;
            return newCopy;
        }
    }

    static final class MutableShort extends MutableValue {
        short value = 0;

        @Override
        Object boxed() {
            return isNull ? null : value;
        }

        @Override
        void update(Object v) {
            isNull = false;
            value = (Short) v;
        }

        @Override
        MutableShort copy() {
            MutableShort newCopy = new MutableShort();
            newCopy.isNull = isNull;
            newCopy.value = value;
            return newCopy;
        }
    }

    static final class MutableLong extends MutableValue {
        long value = 0;

        @Override
        Object boxed() {
            return isNull ? null : value;
        }

        @Override
        void update(Object v) {
            isNull = false;
            value = (Long) v;
        }

        @Override
        MutableLong copy() {
            MutableLong newCopy = new MutableLong();
            newCopy.isNull = isNull;
            newCopy.value = value;
            return newCopy;
        }
    }

    static final class MutableByte extends MutableValue {
        byte value = 0;

        @Override
        Object boxed() {
            return isNull ? null : value;
        }

        @Override
        void update(Object v) {
            isNull = false;
            value = (Byte) v;
        }

        @Override
        MutableByte copy() {
            MutableByte newCopy = new MutableByte();
            newCopy.isNull = isNull;
            newCopy.value = value;
            return newCopy;
        }
    }

    static final class MutableAny extends MutableValue {
        Object value = null;

        @Override
        Object boxed() {
            return isNull ? null : value;
        }

        @Override
        void update(Object v) {
            isNull = false;
            value = v;
        }

        @Override
        MutableAny copy() {
            MutableAny newCopy = new MutableAny();
            newCopy.isNull = isNull;
            newCopy.value = value;
            return newCopy;
        }
    }
}
