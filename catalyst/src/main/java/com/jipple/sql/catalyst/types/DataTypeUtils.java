package com.jipple.sql.catalyst.types;

import com.jipple.sql.types.DataType;

public class DataTypeUtils {

    /**
     * Check if `this` and `other` are the same data type when ignoring nullability
     * (`StructField.nullable`, `ArrayType.containsNull`, and `MapType.valueContainsNull`).
     */
    public static boolean sameType(DataType left, DataType right) {
        return left.sameType(right);
    }

    /**
     * Compares two types, ignoring nullability of ArrayType, MapType, StructType.
     */
    public static boolean equalsIgnoreNullability(DataType left, DataType right) {
        return DataType.equalsIgnoreNullability(left, right);
    }

}
