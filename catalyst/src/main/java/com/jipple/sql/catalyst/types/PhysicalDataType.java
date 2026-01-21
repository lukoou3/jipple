package com.jipple.sql.catalyst.types;

import com.jipple.sql.types.*;

import java.util.Comparator;


public abstract class PhysicalDataType<T> {
    public abstract Comparator<T> comparator();
    public abstract Class<T> internalTypeClass();

    public static PhysicalDataType<?> of (DataType dataType) {
        if (dataType instanceof NullType) {
            return new PhysicalNullType();
        } else if (dataType instanceof IntegerType) {
            return new PhysicalIntegerType();
        } else if (dataType instanceof LongType) {
            return new PhysicalLongType();
        } else if (dataType instanceof StringType) {
            return new PhysicalStringType();
        } else if (dataType instanceof FloatType) {
            return new PhysicalFloatType();
        } else if (dataType instanceof DoubleType) {
            return new PhysicalDoubleType();
        } else if (dataType instanceof DecimalType decimalType) {
            return new PhysicalDecimalType(decimalType.precision, decimalType.scale);
        } else if (dataType instanceof BooleanType) {
            return new PhysicalBooleanType();
        } else if (dataType instanceof BinaryType) {
            return new PhysicalBinaryType();
        } else if (dataType instanceof TimestampType) {
            return new PhysicalLongType();
        } else if (dataType instanceof TimestampNTZType) {
            return new PhysicalLongType();
        } else if (dataType instanceof CalendarIntervalType) {
            return new PhysicalCalendarIntervalType();
        } else if (dataType instanceof DateType) {
            return new PhysicalIntegerType();
        } else if (dataType instanceof ArrayType arrayType) {
            return new PhysicalArrayType(arrayType.elementType, arrayType.containsNull);
        } else if (dataType instanceof StructType structType) {
            return new PhysicalStructType(structType.fields);
        } else if (dataType instanceof MapType mapType) {
            return new PhysicalMapType(mapType.keyType, mapType.valueType, mapType.valueContainsNull);
        } else {
            return new UninitializedPhysicalType();
        }
    }

    public static Comparator<Object> comparator(DataType dataType) {
        return (Comparator<Object>) of(dataType).comparator();
    }

}

