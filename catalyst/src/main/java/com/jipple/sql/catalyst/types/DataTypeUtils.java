package com.jipple.sql.catalyst.types;

import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.expressions.named.AttributeReference;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StructField;
import com.jipple.sql.types.StructType;

import java.util.List;

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

    /**
     * Convert a StructField to a AttributeReference.
     */
    public static Attribute toAttribute(StructField field) {
        return new AttributeReference(field.name, field.dataType, field.nullable);
    }

    /**
     * Convert a [[StructType]] into a Seq of [[AttributeReference]].
     */
    public static List<Attribute> toAttributes(StructType schema) {
        return schema.toAttributes();
    }

}
