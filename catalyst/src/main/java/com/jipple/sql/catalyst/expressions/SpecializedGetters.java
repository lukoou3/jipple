package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.MapData;
import com.jipple.sql.types.DataType;
//import com.jipple.sql.types.Decimal;
import com.jipple.sql.types.Decimal;
import com.jipple.unsafe.types.CalendarInterval;
import com.jipple.unsafe.types.UTF8String;

public interface SpecializedGetters {

  boolean isNullAt(int ordinal);

  boolean getBoolean(int ordinal);

  byte getByte(int ordinal);

  short getShort(int ordinal);

  int getInt(int ordinal);

  long getLong(int ordinal);

  float getFloat(int ordinal);

  double getDouble(int ordinal);

  Decimal getDecimal(int ordinal, int precision, int scale);

  UTF8String getUTF8String(int ordinal);

  byte[] getBinary(int ordinal);

  CalendarInterval getInterval(int ordinal);

  InternalRow getStruct(int ordinal, int numFields);

  ArrayData getArray(int ordinal);

  MapData getMap(int ordinal);

  Object get(int ordinal, DataType dataType);

  Object getObject(int ordinal);
}
