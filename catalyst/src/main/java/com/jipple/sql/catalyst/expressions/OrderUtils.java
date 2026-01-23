package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.types.ArrayType;
import com.jipple.sql.types.AtomicType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.NullType;
import com.jipple.sql.types.StructField;
import com.jipple.sql.types.StructType;

/**
 * Utility methods for ordering.
 */
public final class OrderUtils {
    private OrderUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * Returns true iff the data type can be ordered (i.e. can be sorted).
     */
    public static boolean isOrderable(DataType dataType) {
        if (dataType instanceof NullType) {
            return true;
        } else if (dataType instanceof AtomicType) {
            return true;
        } else if (dataType instanceof StructType) {
            StructType structType = (StructType) dataType;
            for (StructField field : structType.fields) {
                if (!isOrderable(field.dataType)) {
                    return false;
                }
            }
            return true;
        } else if (dataType instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) dataType;
            return isOrderable(arrayType.elementType);
        } else {
            return false;
        }
    }
}
