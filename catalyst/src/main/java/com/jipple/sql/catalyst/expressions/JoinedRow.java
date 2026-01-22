package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.MapData;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.Decimal;
import com.jipple.unsafe.types.CalendarInterval;
import com.jipple.unsafe.types.UTF8String;

/**
 * A mutable wrapper that makes two rows appear as a single concatenated row.  Designed to
 * be instantiated once per thread and reused.
 */
public class JoinedRow extends InternalRow {
    private InternalRow row1;
    private InternalRow row2;

    public JoinedRow() {
    }

    public JoinedRow(InternalRow left, InternalRow right) {
        this.row1 = left;
        this.row2 = right;
    }

    /** Updates this JoinedRow to used point at two new base rows.  Returns itself. */
    public JoinedRow apply(InternalRow r1, InternalRow r2) {
        this.row1 = r1;
        this.row2 = r2;
        return this;
    }

    /** Updates this JoinedRow by updating its left base row.  Returns itself. */
    public JoinedRow withLeft(InternalRow newLeft) {
        this.row1 = newLeft;
        return this;
    }

    /** Updates this JoinedRow by updating its right base row.  Returns itself. */
    public JoinedRow withRight(InternalRow newRight) {
        this.row2 = newRight;
        return this;
    }

    /** Gets this JoinedRow's left base row. */
    public InternalRow getLeft() {
        return row1;
    }

    /** Gets this JoinedRow's right base row. */
    public InternalRow getRight() {
        return row2;
    }

    @Override
    public int numFields() {
        return row1.numFields() + row2.numFields();
    }

    @Override
    public Object get(int i, DataType dt) {
        if (i < row1.numFields()) {
            return row1.get(i, dt);
        } else {
            return row2.get(i - row1.numFields(), dt);
        }
    }

    @Override
    public Object getObject(int i) {
        if (i < row1.numFields()) {
            return row1.getObject(i);
        } else {
            return row2.getObject(i - row1.numFields());
        }
    }

    @Override
    public boolean isNullAt(int i) {
        if (i < row1.numFields()) {
            return row1.isNullAt(i);
        } else {
            return row2.isNullAt(i - row1.numFields());
        }
    }

    @Override
    public boolean getBoolean(int i) {
        if (i < row1.numFields()) {
            return row1.getBoolean(i);
        } else {
            return row2.getBoolean(i - row1.numFields());
        }
    }

    @Override
    public byte getByte(int i) {
        if (i < row1.numFields()) {
            return row1.getByte(i);
        } else {
            return row2.getByte(i - row1.numFields());
        }
    }

    @Override
    public short getShort(int i) {
        if (i < row1.numFields()) {
            return row1.getShort(i);
        } else {
            return row2.getShort(i - row1.numFields());
        }
    }

    @Override
    public int getInt(int i) {
        if (i < row1.numFields()) {
            return row1.getInt(i);
        } else {
            return row2.getInt(i - row1.numFields());
        }
    }

    @Override
    public long getLong(int i) {
        if (i < row1.numFields()) {
            return row1.getLong(i);
        } else {
            return row2.getLong(i - row1.numFields());
        }
    }

    @Override
    public float getFloat(int i) {
        if (i < row1.numFields()) {
            return row1.getFloat(i);
        } else {
            return row2.getFloat(i - row1.numFields());
        }
    }

    @Override
    public double getDouble(int i) {
        if (i < row1.numFields()) {
            return row1.getDouble(i);
        } else {
            return row2.getDouble(i - row1.numFields());
        }
    }

    @Override
    public Decimal getDecimal(int i, int precision, int scale) {
        if (i < row1.numFields()) {
            return row1.getDecimal(i, precision, scale);
        } else {
            return row2.getDecimal(i - row1.numFields(), precision, scale);
        }
    }

    @Override
    public UTF8String getUTF8String(int i) {
        if (i < row1.numFields()) {
            return row1.getUTF8String(i);
        } else {
            return row2.getUTF8String(i - row1.numFields());
        }
    }

    @Override
    public byte[] getBinary(int i) {
        if (i < row1.numFields()) {
            return row1.getBinary(i);
        } else {
            return row2.getBinary(i - row1.numFields());
        }
    }

    @Override
    public ArrayData getArray(int i) {
        if (i < row1.numFields()) {
            return row1.getArray(i);
        } else {
            return row2.getArray(i - row1.numFields());
        }
    }

    @Override
    public CalendarInterval getInterval(int i) {
        if (i < row1.numFields()) {
            return row1.getInterval(i);
        } else {
            return row2.getInterval(i - row1.numFields());
        }
    }

    @Override
    public MapData getMap(int i) {
        if (i < row1.numFields()) {
            return row1.getMap(i);
        } else {
            return row2.getMap(i - row1.numFields());
        }
    }

    @Override
    public InternalRow getStruct(int i, int numFields) {
        if (i < row1.numFields()) {
            return row1.getStruct(i, numFields);
        } else {
            return row2.getStruct(i - row1.numFields(), numFields);
        }
    }

    @Override
    public boolean anyNull() {
        return row1.anyNull() || row2.anyNull();
    }

    @Override
    public void setNullAt(int i) {
        if (i < row1.numFields()) {
            row1.setNullAt(i);
        } else {
            row2.setNullAt(i - row1.numFields());
        }
    }

    @Override
    public void update(int i, Object value) {
        if (i < row1.numFields()) {
            row1.update(i, value);
        } else {
            row2.update(i - row1.numFields(), value);
        }
    }

    @Override
    public InternalRow copy() {
        InternalRow copy1 = row1.copy();
        InternalRow copy2 = row2.copy();
        return new JoinedRow(copy1, copy2);
    }

    @Override
    public String toString() {
        // Make sure toString never throws NullPointerException.
        if (row1 == null && row2 == null) {
            return "[ empty row ]";
        } else if (row1 == null) {
            return row2.toString();
        } else if (row2 == null) {
            return row1.toString();
        } else {
            return "{" + row1.toString() + " + " + row2.toString() + "}";
        }
    }
}

