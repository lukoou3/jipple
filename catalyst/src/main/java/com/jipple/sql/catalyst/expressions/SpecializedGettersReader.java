package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.types.*;
import com.jipple.sql.types.*;

public final class SpecializedGettersReader {
  private SpecializedGettersReader() {}

  public static Object read(
      SpecializedGetters obj,
      int ordinal,
      DataType dataType,
      boolean handleNull,
      boolean handleUserDefinedType) {
    PhysicalDataType physicalDataType = PhysicalDataType.of(dataType);
    if (handleNull && (obj.isNullAt(ordinal) || physicalDataType instanceof PhysicalNullType)) {
      return null;
    }
    if (physicalDataType instanceof PhysicalBooleanType) {
      return obj.getBoolean(ordinal);
    }
    if (physicalDataType instanceof PhysicalIntegerType) {
      return obj.getInt(ordinal);
    }
    if (physicalDataType instanceof PhysicalLongType) {
      return obj.getLong(ordinal);
    }
    if (physicalDataType instanceof PhysicalFloatType) {
      return obj.getFloat(ordinal);
    }
    if (physicalDataType instanceof PhysicalDoubleType) {
      return obj.getDouble(ordinal);
    }
    if (physicalDataType instanceof PhysicalStringType) {
      return obj.getUTF8String(ordinal);
    }
    if (physicalDataType instanceof PhysicalDecimalType) {
      PhysicalDecimalType dt = (PhysicalDecimalType) physicalDataType;
      return obj.getDecimal(ordinal, dt.precision, dt.scale);
    }
    if (physicalDataType instanceof PhysicalCalendarIntervalType) {
      return obj.getInterval(ordinal);
    }
    if (physicalDataType instanceof PhysicalBinaryType) {
      return obj.getBinary(ordinal);
    }
    if (physicalDataType instanceof PhysicalStructType) {
      return obj.getStruct(ordinal, ((PhysicalStructType) physicalDataType).fields.length);
    }
    if (physicalDataType instanceof PhysicalArrayType) {
      return obj.getArray(ordinal);
    }
    if (physicalDataType instanceof PhysicalMapType) {
      return obj.getMap(ordinal);
    }
    /*if (handleUserDefinedType && dataType instanceof UserDefinedType) {
      return obj.get(ordinal, ((UserDefinedType)dataType).sqlType());
    }*/

    throw new UnsupportedOperationException("Unsupported data type " + dataType.simpleString());
  }
}
