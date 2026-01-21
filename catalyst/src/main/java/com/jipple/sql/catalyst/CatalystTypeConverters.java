package com.jipple.sql.catalyst;

import com.jipple.sql.GenericRowWithSchema;
import com.jipple.sql.Row;
import com.jipple.sql.catalyst.expressions.GenericInternalRow;
import com.jipple.sql.catalyst.util.*;
import com.jipple.sql.types.*;
import com.jipple.unsafe.types.UTF8String;
import com.jipple.util.JippleCollectionUtils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * Functions to convert Java types to Catalyst types and vice versa.
 */
public class CatalystTypeConverters {

    private static boolean isPrimitive(DataType dataType) {
        return dataType instanceof BooleanType
                || dataType instanceof IntegerType
                || dataType instanceof LongType
                || dataType instanceof FloatType
                || dataType instanceof DoubleType;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static CatalystTypeConverter<Object, Object, Object> getConverterForType(DataType dataType) {
        if (dataType instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) dataType;
            return (CatalystTypeConverter) new ArrayConverter(arrayType.elementType);
        } else if (dataType instanceof MapType) {
            MapType mapType = (MapType) dataType;
            return (CatalystTypeConverter) new MapConverter(mapType.keyType, mapType.valueType);
        } else if (dataType instanceof StructType) {
            StructType structType = (StructType) dataType;
            return (CatalystTypeConverter) new StructConverter(structType);
        } else if (dataType instanceof StringType) {
            return (CatalystTypeConverter) StringConverter.INSTANCE;
        } else if (dataType instanceof DateType) {
            return (CatalystTypeConverter) DateConverter.INSTANCE;
        } else if (dataType instanceof TimestampType) {
            return (CatalystTypeConverter) TimestampConverter.INSTANCE;
        } else if (dataType instanceof TimestampNTZType) {
            return (CatalystTypeConverter) TimestampNTZConverter.INSTANCE;
        } else if (dataType instanceof DecimalType) {
            DecimalType dt = (DecimalType) dataType;
            return (CatalystTypeConverter) new DecimalConverter(dt);
        } else if (dataType instanceof BooleanType) {
            return (CatalystTypeConverter) BooleanConverter.INSTANCE;
        } else if (dataType instanceof IntegerType) {
            return (CatalystTypeConverter) IntConverter.INSTANCE;
        } else if (dataType instanceof LongType) {
            return (CatalystTypeConverter) LongConverter.INSTANCE;
        } else if (dataType instanceof FloatType) {
            return (CatalystTypeConverter) FloatConverter.INSTANCE;
        } else if (dataType instanceof DoubleType) {
            return (CatalystTypeConverter) DoubleConverter.INSTANCE;
        } else {
            return new IdentityConverter(dataType);
        }
    }

    /**
     * Converts a Java type to its Catalyst equivalent (and vice versa).
     *
     * @param <JavaInputType> The type of Java values that can be converted to Catalyst.
     * @param <JavaOutputType> The type of Java values returned when converting Catalyst to Java.
     * @param <CatalystType> The internal Catalyst type used to represent values of this Java type.
     */
    private abstract static class CatalystTypeConverter<JavaInputType, JavaOutputType, CatalystType>
            implements Serializable {

        /**
         * Converts a Java type to its Catalyst equivalent while automatically handling nulls.
         */
        @SuppressWarnings("unchecked")
        final CatalystType toCatalyst(@Nullable Object maybeScalaValue) {
            if (maybeScalaValue == null) {
                return null;
            }
            return toCatalystImpl((JavaInputType) maybeScalaValue);
        }

        /**
         * Given a Catalyst row, convert the value at column `column` to its Java equivalent.
         */
        final JavaOutputType toJava(InternalRow row, int column) {
            if (row.isNullAt(column)) {
                return null;
            }
            return toJavaImpl(row, column);
        }

        /**
         * Convert a Catalyst value to its Java equivalent.
         */
        abstract JavaOutputType toJava(@Nullable CatalystType catalystValue);

        /**
         * Converts a Java value to its Catalyst equivalent.
         * @param javaValue the Java value, guaranteed not to be null.
         * @return the Catalyst value.
         */
        protected abstract CatalystType toCatalystImpl(JavaInputType javaValue);

        /**
         * Given a Catalyst row, convert the value at column `column` to its Java equivalent.
         * This method will only be called on non-null columns.
         */
        protected abstract JavaOutputType toJavaImpl(InternalRow row, int column);
    }

    private static class IdentityConverter extends CatalystTypeConverter<Object, Object, Object> {
        private final DataType dataType;

        IdentityConverter(DataType dataType) {
            this.dataType = dataType;
        }

        @Override
        protected Object toCatalystImpl(Object javaValue) {
            return javaValue;
        }

        @Override
        public Object toJava(Object catalystValue) {
            return catalystValue;
        }

        @Override
        protected Object toJavaImpl(InternalRow row, int column) {
            return row.get(column, dataType);
        }
    }

    /** Converter for arrays, sequences, and Java iterables. */
    private static class ArrayConverter extends CatalystTypeConverter<Object, List<Object>, ArrayData> {
        private final DataType elementType;
        private final CatalystTypeConverter<Object, Object, Object> elementConverter;

        ArrayConverter(DataType elementType) {
            this.elementType = elementType;
            this.elementConverter = getConverterForType(elementType);
        }

        @Override
        protected ArrayData toCatalystImpl(Object javaValue) {
            if (javaValue.getClass().isArray()) {
                Object[] arr = (Object[]) javaValue;
                Object[] converted = new Object[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    converted[i] = elementConverter.toCatalyst(arr[i]);
                }
                return new GenericArrayData(converted);
            } else if (javaValue instanceof List) {
                List<?> list = (List<?>) javaValue;
                Object[] converted = new Object[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    converted[i] = elementConverter.toCatalyst(list.get(i));
                }
                return new GenericArrayData(converted);
            } else if (javaValue instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) javaValue;
                List<Object> convertedList = new ArrayList<>();
                for (Object item : iterable) {
                    convertedList.add(elementConverter.toCatalyst(item));
                }
                return new GenericArrayData(convertedList.toArray(new Object[0]));
            } else {
                throw new IllegalArgumentException(
                        "The value (" + javaValue.toString() + ") of the type (" + javaValue.getClass().getCanonicalName() + ") "
                                + "cannot be converted to an array of " + elementType.simpleString());
            }
        }

        @Override
        public List<Object> toJava(ArrayData catalystValue) {
            if (catalystValue == null) {
                return null;
            } else if (isPrimitive(elementType)) {
                Object[] arr = catalystValue.toArray(elementType);
                return Arrays.asList(arr);
            } else {
                Object[] result = new Object[catalystValue.numElements()];
                catalystValue.foreach(elementType, (i, e) -> {
                    result[i] = elementConverter.toJava(e);
                });
                return Arrays.asList(result);
            }
        }

        @Override
        protected List<Object> toJavaImpl(InternalRow row, int column) {
            return toJava(row.getArray(column));
        }
    }

    private static class MapConverter extends CatalystTypeConverter<Object, Map<Object, Object>, MapData> {
        private final DataType keyType;
        private final DataType valueType;
        private final CatalystTypeConverter<Object, Object, Object> keyConverter;
        private final CatalystTypeConverter<Object, Object, Object> valueConverter;

        MapConverter(DataType keyType, DataType valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
            this.keyConverter = getConverterForType(keyType);
            this.valueConverter = getConverterForType(valueType);
        }

        @Override
        protected MapData toCatalystImpl(Object javaValue) {
            Function<Object, Object> keyFunction = keyConverter::toCatalyst;
            Function<Object, Object> valueFunction = valueConverter::toCatalyst;

            if (javaValue instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) javaValue;
                return ArrayBasedMapData.fromMap(map, keyFunction, valueFunction);
            } else {
                throw new IllegalArgumentException(
                        "The value (" + javaValue.toString() + ") of the type (" + javaValue.getClass().getCanonicalName() + ") "
                                + "cannot be converted to a map type with "
                                + "key type (" + keyType.simpleString() + ") and value type (" + valueType.simpleString() + ")");
            }
        }

        @Override
        public Map<Object, Object> toJava(MapData catalystValue) {
            if (catalystValue == null) {
                return null;
            } else {
                Object[] keys = catalystValue.keyArray().toArray(keyType);
                Object[] values = catalystValue.valueArray().toArray(valueType);
                Object[] convertedKeys;
                if (isPrimitive(keyType)) {
                    convertedKeys = keys;
                } else {
                    convertedKeys = new Object[keys.length];
                    for (int i = 0; i < keys.length; i++) {
                        convertedKeys[i] = keyConverter.toJava(keys[i]);
                    }
                }
                Object[] convertedValues;
                if (isPrimitive(valueType)) {
                    convertedValues = values;
                } else {
                    convertedValues = new Object[values.length];
                    for (int i = 0; i < values.length; i++) {
                        convertedValues[i] = valueConverter.toJava(values[i]);
                    }
                }

                return JippleCollectionUtils.toMap(Arrays.asList(convertedKeys), Arrays.asList(convertedValues));
            }
        }

        @Override
        protected Map<Object, Object> toJavaImpl(InternalRow row, int column) {
            return toJava(row.getMap(column));
        }
    }

    private static class StructConverter extends CatalystTypeConverter<Object, Row, InternalRow> {
        private final StructType structType;
        private final CatalystTypeConverter<Object, Object, Object>[] converters;

        @SuppressWarnings("unchecked")
        StructConverter(StructType structType) {
            this.structType = structType;
            this.converters = new CatalystTypeConverter[structType.fields.length];
            for (int i = 0; i < structType.fields.length; i++) {
                converters[i] = getConverterForType(structType.fields[i].dataType);
            }
        }

        @Override
        protected InternalRow toCatalystImpl(Object javaValue) {
            if (javaValue instanceof Row) {
                Row row = (Row) javaValue;
                Object[] ar = new Object[row.size()];
                for (int idx = 0; idx < row.size(); idx++) {
                    ar[idx] = converters[idx].toCatalyst(row.get(idx));
                }
                return new GenericInternalRow(ar);
            } else {
                throw new IllegalArgumentException(
                        "The value (" + javaValue.toString() + ") of the type (" + javaValue.getClass().getCanonicalName() + ") "
                                + "cannot be converted to " + structType.simpleString());
            }
        }

        @Override
        public Row toJava(InternalRow row) {
            if (row == null) {
                return null;
            } else {
                Object[] ar = new Object[row.numFields()];
                for (int idx = 0; idx < row.numFields(); idx++) {
                    ar[idx] = converters[idx].toJava(row, idx);
                }
                return new GenericRowWithSchema(ar, structType);
            }
        }

        @Override
        protected Row toJavaImpl(InternalRow row, int column) {
            return toJava(row.getStruct(column, structType.fields.length));
        }
    }

    private static class StringConverter extends CatalystTypeConverter<Object, String, UTF8String> {
        static final StringConverter INSTANCE = new StringConverter();

        @Override
        protected UTF8String toCatalystImpl(Object javaValue) {
            if (javaValue instanceof String) {
                return UTF8String.fromString((String) javaValue);
            } else if (javaValue instanceof UTF8String) {
                return (UTF8String) javaValue;
            } else if (javaValue instanceof Character) {
                return UTF8String.fromString(javaValue.toString());
            } else if (javaValue instanceof char[]) {
                return UTF8String.fromString(String.valueOf((char[]) javaValue));
            } else {
                throw new IllegalArgumentException(
                        "The value (" + javaValue.toString() + ") of the type (" + javaValue.getClass().getCanonicalName() + ") "
                                + "cannot be converted to the string type");
            }
        }

        @Override
        public String toJava(UTF8String catalystValue) {
            return catalystValue == null ? null : catalystValue.toString();
        }

        @Override
        protected String toJavaImpl(InternalRow row, int column) {
            return row.getUTF8String(column).toString();
        }
    }

    private static class DateConverter extends CatalystTypeConverter<Object, Date, Object> {
        static final DateConverter INSTANCE = new DateConverter();

        @Override
        protected Object toCatalystImpl(Object javaValue) {
            if (javaValue instanceof Date) {
                return DateTimeUtils.fromJavaDate((Date) javaValue);
            } else if (javaValue instanceof LocalDate) {
                return DateTimeUtils.localDateToDays((LocalDate) javaValue);
            } else {
                throw new IllegalArgumentException(
                        "The value (" + javaValue.toString() + ") of the type (" + javaValue.getClass().getCanonicalName() + ") "
                                + "cannot be converted to the " + DateType.INSTANCE.sql() + " type");
            }
        }

        @Override
        public Date toJava(Object catalystValue) {
            return catalystValue == null ? null : DateTimeUtils.toJavaDate((Integer) catalystValue);
        }

        @Override
        protected Date toJavaImpl(InternalRow row, int column) {
            return DateTimeUtils.toJavaDate(row.getInt(column));
        }
    }

    private static class LocalDateConverter extends CatalystTypeConverter<Object, LocalDate, Object> {
        static final LocalDateConverter INSTANCE = new LocalDateConverter();

        @Override
        protected Object toCatalystImpl(Object javaValue) {
            return DateConverter.INSTANCE.toCatalystImpl(javaValue);
        }

        @Override
        public LocalDate toJava(Object catalystValue) {
            return catalystValue == null ? null : DateTimeUtils.daysToLocalDate((Integer) catalystValue);
        }

        @Override
        protected LocalDate toJavaImpl(InternalRow row, int column) {
            return DateTimeUtils.daysToLocalDate(row.getInt(column));
        }
    }

    private static class TimestampConverter extends CatalystTypeConverter<Object, Timestamp, Object> {
        static final TimestampConverter INSTANCE = new TimestampConverter();

        @Override
        protected Object toCatalystImpl(Object javaValue) {
            if (javaValue instanceof Timestamp) {
                return DateTimeUtils.fromJavaTimestamp((Timestamp) javaValue);
            } else if (javaValue instanceof Instant) {
                return DateTimeUtils.instantToMicros((Instant) javaValue);
            } else {
                throw new IllegalArgumentException(
                        "The value (" + javaValue.toString() + ") of the type (" + javaValue.getClass().getCanonicalName() + ") "
                                + "cannot be converted to the " + TimestampType.INSTANCE.sql() + " type");
            }
        }

        @Override
        public Timestamp toJava(Object catalystValue) {
            return catalystValue == null ? null : DateTimeUtils.toJavaTimestamp((Long) catalystValue);
        }

        @Override
        protected Timestamp toJavaImpl(InternalRow row, int column) {
            return DateTimeUtils.toJavaTimestamp(row.getLong(column));
        }
    }

    private static class InstantConverter extends CatalystTypeConverter<Object, Instant, Object> {
        static final InstantConverter INSTANCE = new InstantConverter();

        @Override
        protected Object toCatalystImpl(Object javaValue) {
            return TimestampConverter.INSTANCE.toCatalystImpl(javaValue);
        }

        @Override
        public Instant toJava(Object catalystValue) {
            return catalystValue == null ? null : DateTimeUtils.microsToInstant((Long) catalystValue);
        }

        @Override
        protected Instant toJavaImpl(InternalRow row, int column) {
            return DateTimeUtils.microsToInstant(row.getLong(column));
        }
    }

    private static class TimestampNTZConverter extends CatalystTypeConverter<Object, LocalDateTime, Object> {
        static final TimestampNTZConverter INSTANCE = new TimestampNTZConverter();

        @Override
        protected Object toCatalystImpl(Object javaValue) {
            if (javaValue instanceof LocalDateTime) {
                return DateTimeUtils.localDateTimeToMicros((LocalDateTime) javaValue);
            } else {
                throw new IllegalArgumentException(
                        "The value (" + javaValue.toString() + ") of the type (" + javaValue.getClass().getCanonicalName() + ") "
                                + "cannot be converted to the " + TimestampNTZType.INSTANCE.sql() + " type");
            }
        }

        @Override
        public LocalDateTime toJava(Object catalystValue) {
            return catalystValue == null ? null : DateTimeUtils.microsToLocalDateTime((Long) catalystValue);
        }

        @Override
        protected LocalDateTime toJavaImpl(InternalRow row, int column) {
            return DateTimeUtils.microsToLocalDateTime(row.getLong(column));
        }
    }

    private static class DecimalConverter extends CatalystTypeConverter<Object, BigDecimal, Decimal> {
        private final DecimalType dataType;
        private final boolean nullOnOverflow = true;

        DecimalConverter(DecimalType dataType) {
            this.dataType = dataType;
        }

        @Override
        protected Decimal toCatalystImpl(Object javaValue) {
            Decimal decimal;
            if (javaValue instanceof BigDecimal) {
                decimal = new Decimal((BigDecimal) javaValue);
            } else if (javaValue instanceof BigInteger) {
                decimal = new Decimal((BigInteger) javaValue);
            } else if (javaValue instanceof Decimal) {
                decimal = (Decimal) javaValue;
            } else {
                throw new IllegalArgumentException(
                        "The value (" + javaValue.toString() + ") of the type (" + javaValue.getClass().getCanonicalName() + ") "
                                + "cannot be converted to " + dataType.simpleString());
            }
            return decimal.toPrecision(dataType.precision, dataType.scale, java.math.RoundingMode.HALF_UP, nullOnOverflow);
        }

        @Override
        public BigDecimal toJava(Decimal catalystValue) {
            return catalystValue == null ? null : catalystValue.toBigDecimal();
        }

        @Override
        protected BigDecimal toJavaImpl(InternalRow row, int column) {
            return row.getDecimal(column, dataType.precision, dataType.scale).toBigDecimal();
        }
    }

    private abstract static class PrimitiveConverter<T> extends CatalystTypeConverter<T, Object, Object> {
        @Override
        public final Object toJava(Object catalystValue) {
            return catalystValue;
        }

        @Override
        protected final Object toCatalystImpl(T javaValue) {
            return javaValue;
        }
    }

    private static class BooleanConverter extends PrimitiveConverter<Boolean> {
        static final BooleanConverter INSTANCE = new BooleanConverter();

        @Override
        protected Boolean toJavaImpl(InternalRow row, int column) {
            return row.getBoolean(column);
        }
    }

    private static class IntConverter extends PrimitiveConverter<Integer> {
        static final IntConverter INSTANCE = new IntConverter();

        @Override
        protected Integer toJavaImpl(InternalRow row, int column) {
            return row.getInt(column);
        }
    }

    private static class LongConverter extends PrimitiveConverter<Long> {
        static final LongConverter INSTANCE = new LongConverter();

        @Override
        protected Long toJavaImpl(InternalRow row, int column) {
            return row.getLong(column);
        }
    }

    private static class FloatConverter extends PrimitiveConverter<Float> {
        static final FloatConverter INSTANCE = new FloatConverter();

        @Override
        protected Float toJavaImpl(InternalRow row, int column) {
            return row.getFloat(column);
        }
    }

    private static class DoubleConverter extends PrimitiveConverter<Double> {
        static final DoubleConverter INSTANCE = new DoubleConverter();

        @Override
        protected Double toJavaImpl(InternalRow row, int column) {
            return row.getDouble(column);
        }
    }

    /**
     * Creates a converter function that will convert Java objects to the specified Catalyst type.
     * Typical use case would be converting a collection of rows that have the same schema. You will
     * call this function once to get a converter, and apply it to every row.
     */
    public static Function<Object, Object> createToCatalystConverter(DataType dataType) {
        if (isPrimitive(dataType)) {
            return Function.identity();
        } else {
            CatalystTypeConverter<Object, Object, Object> converter = getConverterForType(dataType);
            return converter::toCatalyst;
        }
    }

    /**
     * Creates a converter function that will convert Catalyst types to Java type.
     * Typical use case would be converting a collection of rows that have the same schema. You will
     * call this function once to get a converter, and apply it to every row.
     */
    public static Function<Object, Object> createToJavaConverter(DataType dataType) {
        if (isPrimitive(dataType)) {
            return Function.identity();
        } else {
            CatalystTypeConverter<Object, Object, Object> converter = getConverterForType(dataType);
            return converter::toJava;
        }
    }

    /**
     * Converts Java objects to Catalyst rows / types.
     *
     * Note: This should be called before do evaluation on Row
     *        (It does not support UDT)
     * This is used to create an RDD or test results with correct types for Catalyst.
     */
    public static Object convertToCatalyst(Object a) {
        if (a instanceof String) {
            return StringConverter.INSTANCE.toCatalyst(a);
        } else if (a instanceof Character) {
            return StringConverter.INSTANCE.toCatalyst(a.toString());
        } else if (a instanceof Date) {
            return DateConverter.INSTANCE.toCatalyst(a);
        } else if (a instanceof LocalDate) {
            return LocalDateConverter.INSTANCE.toCatalyst(a);
        } else if (a instanceof Timestamp) {
            return TimestampConverter.INSTANCE.toCatalyst(a);
        } else if (a instanceof Instant) {
            return InstantConverter.INSTANCE.toCatalyst(a);
        } else if (a instanceof LocalDateTime) {
            return TimestampNTZConverter.INSTANCE.toCatalyst(a);
        } else if (a instanceof BigDecimal) {
            BigDecimal d = (BigDecimal) a;
            return new DecimalConverter(new DecimalType(Math.max(d.precision(), d.scale()), d.scale())).toCatalyst(d);
        } else if (a instanceof List) {
            List<?> seq = (List<?>) a;
            Object[] converted = new Object[seq.size()];
            for (int i = 0; i < seq.size(); i++) {
                converted[i] = convertToCatalyst(seq.get(i));
            }
            return new GenericArrayData(converted);
        } else if (a instanceof Row) {
            Row r = (Row) a;
            Object[] converted = new Object[r.size()];
            for (int i = 0; i < r.size(); i++) {
                converted[i] = convertToCatalyst(r.get(i));
            }
            return new GenericInternalRow(converted);
        } else if (a instanceof byte[]) {
            return a;
        } else if (a instanceof char[]) {
            return StringConverter.INSTANCE.toCatalyst(a);
        } else if (a != null && a.getClass().isArray()) {
            Object[] arr = (Object[]) a;
            Object[] converted = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                converted[i] = convertToCatalyst(arr[i]);
            }
            return new GenericArrayData(converted);
        } else if (a instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) a;
            return ArrayBasedMapData.fromMap(
                    map,
                    CatalystTypeConverters::convertToCatalyst,
                    CatalystTypeConverters::convertToCatalyst);
        } else {
            return a;
        }
    }

    /**
     * Converts Catalyst types used internally in rows to standard Java types
     * This method is slow, and for batch conversion you should be using converter
     * produced by createToScalaConverter.
     */
    public static Object convertToJava(Object catalystValue, DataType dataType) {
        return createToJavaConverter(dataType).apply(catalystValue);
    }
}

