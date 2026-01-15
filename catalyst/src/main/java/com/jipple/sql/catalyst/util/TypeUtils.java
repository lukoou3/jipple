package com.jipple.sql.catalyst.util;

import com.jipple.sql.catalyst.types.PhysicalDataType;
import com.jipple.sql.types.DataType;

import java.util.Comparator;

/**
 * Functions to help with checking for valid data types and value comparison of various types.
 */
public class TypeUtils {

  public static Comparator<Object> getInterpretedComparator(DataType t) {
      return (Comparator<Object>) PhysicalDataType.of(t).comparator();
  }

}
