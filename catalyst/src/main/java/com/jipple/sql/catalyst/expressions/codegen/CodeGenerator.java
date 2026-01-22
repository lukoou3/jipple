package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.types.*;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.MapData;
import com.jipple.sql.types.*;
import com.jipple.unsafe.types.CalendarInterval;
import com.jipple.unsafe.types.UTF8String;

/**
 * CodeGenerator interface providing utility methods for code generation.
 */
public interface CodeGenerator {
    // Java primitive type names
    String JAVA_BOOLEAN = "boolean";
    String JAVA_BYTE = "byte";
    String JAVA_SHORT = "short";
    String JAVA_INT = "int";
    String JAVA_LONG = "long";
    String JAVA_FLOAT = "float";
    String JAVA_DOUBLE = "double";

    /**
     * Returns the Java Class for a DataType.
     */
    static Class<?> javaClass(DataType dt) {
        if (dt instanceof BooleanType) {
            return Boolean.TYPE;
        } else if (dt instanceof IntegerType || dt instanceof DateType) {
            return Integer.TYPE;
        } else if (dt instanceof LongType || dt instanceof TimestampType || dt instanceof TimestampNTZType) {
            return Long.TYPE;
        } else if (dt instanceof FloatType) {
            return Float.TYPE;
        } else if (dt instanceof DoubleType) {
            return Double.TYPE;
        } else if (dt instanceof DecimalType) {
            return Decimal.class;
        } else if (dt instanceof BinaryType) {
            return byte[].class;
        } else if (dt instanceof StringType) {
            return UTF8String.class;
        } else if (dt instanceof CalendarIntervalType) {
            return CalendarInterval.class;
        } else if (dt instanceof StructType) {
            return InternalRow.class;
        } else if (dt instanceof ArrayType) {
            return ArrayData.class;
        } else if (dt instanceof MapType) {
            return MapData.class;
        } else {
            return Object.class;
        }
    }

    /**
     * Returns the Java type string for a DataType.
     */
    static String javaType(DataType dt) {
        PhysicalDataType<?> physicalType = PhysicalDataType.of(dt);
        if (physicalType instanceof PhysicalBooleanType) {
            return JAVA_BOOLEAN;
        } else if (physicalType instanceof PhysicalIntegerType) {
            return JAVA_INT;
        } else if (physicalType instanceof PhysicalLongType) {
            return JAVA_LONG;
        } else if (physicalType instanceof PhysicalFloatType) {
            return JAVA_FLOAT;
        } else if (physicalType instanceof PhysicalDoubleType) {
            return JAVA_DOUBLE;
        } else if (physicalType instanceof PhysicalBinaryType) {
            return "byte[]";
        } else if (physicalType instanceof PhysicalCalendarIntervalType) {
            return "CalendarInterval";
        } else if (physicalType instanceof PhysicalDecimalType) {
            return "Decimal";
        } else if (physicalType instanceof PhysicalStringType) {
            return "UTF8String";
        } else if (physicalType instanceof PhysicalArrayType) {
            return "ArrayData";
        } else if (physicalType instanceof PhysicalMapType) {
            return "MapData";
        } else if (physicalType instanceof PhysicalStructType) {
            return "InternalRow";
        } else {
            return "Object";
        }
    }

    /**
     * Returns the boxed type in Java for a Java type string.
     */
    static String boxedType(String jt) {
        switch (jt) {
            case JAVA_BOOLEAN:
                return "Boolean";
            case JAVA_BYTE:
                return "Byte";
            case JAVA_SHORT:
                return "Short";
            case JAVA_INT:
                return "Integer";
            case JAVA_LONG:
                return "Long";
            case JAVA_FLOAT:
                return "Float";
            case JAVA_DOUBLE:
                return "Double";
            default:
                return jt;
        }
    }

    /**
     * Returns the boxed Java type string for a DataType.
     */
    static String boxedType(DataType dt) {
        return boxedType(javaType(dt));
    }

    /**
     * Returns the representation of default value for a given Java Type.
     * @param jt the string name of the Java type
     * @param typedNull if true, for null literals, return a typed (with a cast) version
     */
    static String defaultValue(String jt, boolean typedNull) {
        switch (jt) {
            case JAVA_BOOLEAN:
                return "false";
            case JAVA_BYTE:
                return "(byte)-1";
            case JAVA_SHORT:
                return "(short)-1";
            case JAVA_INT:
                return "-1";
            case JAVA_LONG:
                return "-1L";
            case JAVA_FLOAT:
                return "-1.0f";
            case JAVA_DOUBLE:
                return "-1.0";
            default:
                if (typedNull) {
                    return "((" + jt + ")null)";
                } else {
                    return "null";
                }
        }
    }

    /**
     * Returns the default value string for a DataType.
     * @param dt the DataType
     * @param typedNull if true, for null literals, return a typed (with a cast) version
     */
    static String defaultValue(DataType dt, boolean typedNull) {
        return defaultValue(javaType(dt), typedNull);
    }
}

