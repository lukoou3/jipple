package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.util.*;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.sql.types.*;
import com.jipple.unsafe.UTF8StringBuilder;
import com.jipple.unsafe.types.UTF8String;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND;

public class Cast extends UnaryExpression implements TimeZoneAwareExpression {
    public final static String nullString = "null";
    public final static String leftBracket = "{";
    public final static String rightBracket = "}";
    public final DataType dataType;
    public final Option<String> timeZoneId;
    private transient Function<Object, Object> _cast;
    private transient DateFormatter dateFormatter;
    private transient TimestampFormatter timestampFormatter;
    private transient TimestampFormatter timestampNTZFormatter;

    public Cast(Expression child, DataType dataType) {
        this(child, dataType, Option.empty());
    }

    public Cast(Expression child, DataType dataType, Option<String> timeZoneId) {
        super(child);
        this.dataType = dataType;
        this.timeZoneId = timeZoneId;
    }

    @Override
    public Object[] args() {
        return new Object[]{child, dataType, timeZoneId};
    }

    @Override
    public DataType dataType() {
        return dataType;
    }

    @Override
    public Option<String> timeZoneId() {
        return timeZoneId;
    }

    @Override
    public Cast withTimeZone(String timeZoneId) {
        return new Cast(child, dataType, Option.of(timeZoneId));
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        boolean canCast = Cast.canCast(child.dataType(), dataType);
        if (canCast) {
            return TypeCheckResult.typeCheckSuccess();
        } else {
            return TypeCheckResult.typeCheckFailure("Cannot cast " + child.dataType() + " to " + dataType);
        }
    }

    @Override
    public boolean nullable() {
        return child.nullable() || Cast.forceNullable(child.dataType(), dataType);
    }

    // When this cast involves TimeZone, it's only resolved if the timeZoneId is set;
    // Otherwise behave like Expression.resolved.

    @Override
    public boolean resolved() {
        return childrenResolved() && checkInputDataTypes().isSuccess() && (!needsTimeZone() || timeZoneId.isDefined());
    }

    public boolean needsTimeZone() {
        return Cast.needsTimeZone(child.dataType(), dataType);
    }

    // BinaryConverter
    private Function<Object, Object> castToBinary(DataType from) {
        if (from instanceof StringType) {
            return x -> ((UTF8String) x).getBytes();
        } else if (from instanceof IntegerType) {
            return x -> NumberConverter.toBinary((Integer) x);
        } else if (from instanceof LongType) {
            return x -> NumberConverter.toBinary((Long) x);
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to BinaryType");
        }
    }

    private Function<Object, Object> castToBoolean(DataType from) {
        if (from instanceof StringType) {
            return x -> {
                UTF8String s = (UTF8String) x;
                if (StringUtils.isTrueString(s)) {
                    return true;
                } else if (StringUtils.isFalseString(s)) {
                    return false;
                } else {
                    return null;
                }
            };
        } else if (from instanceof TimestampType) {
            return x -> ((Long) x) != 0;
        } else if (from instanceof DateType) {
            // Hive would return null when cast from date to boolean
            return x -> null;
        } else if (from instanceof LongType) {
            return x -> ((Long) x) != 0;
        } else if (from instanceof IntegerType) {
            return x -> ((Integer) x) != 0;
        } else if (from instanceof DecimalType) {
            return x -> ((Decimal) x) != Decimal.ZERO;
        } else if (from instanceof DoubleType) {
            return x -> ((Double) x) != 0;
        } else if (from instanceof FloatType) {
            return x -> ((Float) x) != 0;
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to BooleanType");
        }
    }

    // TimestampConverter
    private Function<Object, Object> castToTimestamp(DataType from) {
        if (from instanceof StringType) {
            return x -> {
                UTF8String utfs = (UTF8String) x;
                return JippleDateTimeUtils.stringToTimestamp(utfs, zoneId()).getOrElse( null);
            };
        } else if (from instanceof BooleanType) {
            return x -> ((Boolean) x) ? 1L : 0L;
        } else if (from instanceof LongType) {
            return x -> longToTimestamp((Long) x);
        } else if (from instanceof IntegerType) {
            return x -> longToTimestamp(((Integer) x).longValue());
        } else if (from instanceof DateType) {
            return x -> JippleDateTimeUtils.daysToMicros((Integer) x, zoneId());
        } else if (from instanceof TimestampNTZType) {
            return x -> JippleDateTimeUtils.convertTz((Long) x, zoneId(), ZoneOffset.UTC);
        } else if (from instanceof DecimalType) {
            return x -> decimalToTimestamp((Decimal) x);
        } else if (from instanceof DoubleType) {
            // Note: ansiEnabled is not available, using non-ansi version
            return x -> doubleToTimestamp((Double) x);
        } else if (from instanceof FloatType) {
            // Note: ansiEnabled is not available, using non-ansi version
            return x -> doubleToTimestamp(((Float) x).doubleValue());
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to TimestampType");
        }
    }

    /**
     * Converts timestamp to TimestampNTZType.
     */
    private Function<Object, Object> castToTimestampNTZ(DataType from) {
        if (from instanceof StringType) {
            return x -> {
                UTF8String utfs = (UTF8String) x;
                // Parse without timezone - use UTC as default
                Option<Long> result = JippleDateTimeUtils.stringToTimestamp(utfs, ZoneOffset.UTC);
                return result.isDefined() ? result.get() : null;
            };
        } else if (from instanceof DateType) {
            return x -> JippleDateTimeUtils.daysToMicros((Integer) x, ZoneOffset.UTC);
        } else if (from instanceof TimestampType) {
            return x -> JippleDateTimeUtils.convertTz((Long) x, ZoneOffset.UTC, zoneId());
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to TimestampNTZType");
        }
    }

    // Helper methods for timestamp conversion
    /**
     * Converts seconds to microseconds.
     */
    private long longToTimestamp(long seconds) {
        return TimeUnit.SECONDS.toMicros(seconds);
    }

    /**
     * Converts microseconds to seconds (long).
     */
    private long timestampToLong(long micros) {
        return Math.floorDiv(micros, MICROS_PER_SECOND);
    }

    /**
     * Converts microseconds to seconds (double).
     */
    private double timestampToDouble(long micros) {
        return (double) micros / MICROS_PER_SECOND;
    }

    /**
     * Converts Decimal to timestamp (microseconds).
     */
    private Long decimalToTimestamp(Decimal d) {
        return d.toBigDecimal().multiply(java.math.BigDecimal.valueOf(MICROS_PER_SECOND)).longValue();
    }

    /**
     * Converts Double to timestamp (microseconds).
     * Returns null if the value is NaN or infinite.
     */
    private Long doubleToTimestamp(double d) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            return null;
        }
        return (long) (d * MICROS_PER_SECOND);
    }

    /**
     * Processes floating point special literals such as 'Infinity', 'Inf', '-Infinity' and 'NaN'
     * in case insensitive manner to be compatible with other database systems such as PostgreSQL and DB2.
     * 
     * @param v the string value to process
     * @param isFloat true if converting to float, false if converting to double
     * @return the special value (Float/Double infinity or NaN) or null if not a special literal
     */
    public static Object processFloatingPointSpecialLiterals(String v, boolean isFloat) {
        String normalized = v.trim().toLowerCase(java.util.Locale.ROOT);
        switch (normalized) {
            case "inf":
            case "+inf":
            case "infinity":
            case "+infinity":
                return isFloat ? Float.POSITIVE_INFINITY : Double.POSITIVE_INFINITY;
            case "-inf":
            case "-infinity":
                return isFloat ? Float.NEGATIVE_INFINITY : Double.NEGATIVE_INFINITY;
            case "nan":
                return isFloat ? Float.NaN : Double.NaN;
            default:
                return null;
        }
    }

    /**
     * Converts to LongType.
     */
    private Function<Object, Object> castToLong(DataType from) {
        if (from instanceof StringType) {
            return x -> {
                UTF8String s = (UTF8String) x;
                UTF8String.LongWrapper result = new UTF8String.LongWrapper();
                return s.toLong(result) ? result.value : null;
            };
        } else if (from instanceof BooleanType) {
            return x -> ((Boolean) x) ? 1L : 0L;
        } else if (from instanceof DateType) {
            return x -> null; // DateType cannot be cast to LongType
        } else if (from instanceof TimestampType) {
            return x -> timestampToLong((Long) x);
        } else if (from instanceof NumericType) {
            // Handle different numeric types
            if (from instanceof LongType) {
                return x -> x; // No conversion needed
            } else if (from instanceof IntegerType) {
                return x -> ((Integer) x).longValue();
            } else if (from instanceof DoubleType) {
                return x -> ((Double) x).longValue();
            } else if (from instanceof FloatType) {
                return x -> ((Float) x).longValue();
            } else if (from instanceof DecimalType) {
                return x -> ((Decimal) x).toLong();
            } else {
                throw new UnsupportedOperationException("Cannot cast " + from + " to LongType");
            }
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to LongType");
        }
    }

    /**
     * Converts to IntegerType.
     */
    private Function<Object, Object> castToInt(DataType from) {
        if (from instanceof StringType) {
            return x -> {
                UTF8String s = (UTF8String) x;
                UTF8String.IntWrapper result = new UTF8String.IntWrapper();
                return s.toInt(result) ? result.value : null;
            };
        } else if (from instanceof BooleanType) {
            return x -> ((Boolean) x) ? 1 : 0;
        } else if (from instanceof DateType) {
            return x -> null; // DateType cannot be cast to IntegerType
        } else if (from instanceof TimestampType) {
            return x -> {
                long micros = (Long) x;
                return (int) timestampToLong(micros);
            };
        } else if (from instanceof NumericType) {
            // Handle different numeric types
            if (from instanceof IntegerType) {
                return x -> x; // No conversion needed
            } else if (from instanceof LongType) {
                return x -> ((Long) x).intValue();
            } else if (from instanceof DoubleType) {
                return x -> ((Double) x).intValue();
            } else if (from instanceof FloatType) {
                return x -> ((Float) x).intValue();
            } else if (from instanceof DecimalType) {
                return x -> ((Decimal) x).toInt();
            } else {
                throw new UnsupportedOperationException("Cannot cast " + from + " to IntegerType");
            }
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to IntegerType");
        }
    }

    /**
     * Converts to DoubleType.
     */
    private Function<Object, Object> castToDouble(DataType from) {
        if (from instanceof StringType) {
            return x -> {
                UTF8String s = (UTF8String) x;
                String doubleStr = s.toString();
                try {
                    return Double.parseDouble(doubleStr);
                } catch (NumberFormatException e) {
                    Object special = processFloatingPointSpecialLiterals(doubleStr, false);
                    return special != null ? special : null;
                }
            };
        } else if (from instanceof BooleanType) {
            return x -> ((Boolean) x) ? 1.0 : 0.0;
        } else if (from instanceof DateType) {
            return x -> null; // DateType cannot be cast to DoubleType
        } else if (from instanceof TimestampType) {
            return x -> timestampToDouble((Long) x);
        } else if (from instanceof NumericType) {
            // Handle different numeric types
            if (from instanceof DoubleType) {
                return x -> x; // No conversion needed
            } else if (from instanceof FloatType) {
                return x -> ((Float) x).doubleValue();
            } else if (from instanceof LongType) {
                return x -> ((Long) x).doubleValue();
            } else if (from instanceof IntegerType) {
                return x -> ((Integer) x).doubleValue();
            } else if (from instanceof DecimalType) {
                return x -> ((Decimal) x).toDouble();
            } else {
                throw new UnsupportedOperationException("Cannot cast " + from + " to DoubleType");
            }
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to DoubleType");
        }
    }

    /**
     * Changes the precision / scale in a given decimal to those set in decimalType.
     * If an overflow occurs, returns null (since ansiEnabled is false).
     * 
     * NOTE: this modifies value in-place, so don't call it on external data.
     */
    private Decimal changePrecision(Decimal value, DecimalType decimalType) {
        if (value.changePrecision(decimalType.precision, decimalType.scale)) {
            return value;
        } else {
            // nullOnOverflow is true since ansiEnabled is false
            return null;
        }
    }

    /**
     * Creates new Decimal with precision and scale given in decimalType.
     * If overflow occurs, returns null (since ansiEnabled is false).
     */
    private Decimal toPrecision(Decimal value, DecimalType decimalType) {
        return value.toPrecision(decimalType.precision, decimalType.scale,java.math.RoundingMode.HALF_UP, true);
    }

    /**
     * Converts to DecimalType.
     */
    private Function<Object, Object> castToDecimal(DataType from, DecimalType target) {
        if (from instanceof StringType) {
            // Since ansiEnabled is false, use fromString (non-ANSI version)
            return x -> {
                UTF8String s = (UTF8String) x;
                Decimal d = Decimal.fromString(s);
                if (d == null) {
                    return null;
                } else {
                    return changePrecision(d, target);
                }
            };
        } else if (from instanceof BooleanType) {
            return x -> {
                boolean b = (Boolean) x;
                Decimal value = b ? Decimal.ONE : Decimal.ZERO;
                return toPrecision(value, target);
            };
        } else if (from instanceof DateType) {
            // Date can't cast to decimal in Hive
            return x -> null;
        } else if (from instanceof TimestampType) {
            // Note that we lose precision here.
            return x -> {
                long t = (Long) x;
                double d = timestampToDouble(t);
                Decimal decimal = new Decimal(d);
                return changePrecision(decimal, target);
            };
        } else if (from instanceof DecimalType) {
            return x -> toPrecision((Decimal) x, target);
        } else if (from instanceof LongType) {
            return x -> {
                long l = (Long) x;
                Decimal decimal = new Decimal(l);
                return changePrecision(decimal, target);
            };
        } else if (from instanceof IntegerType) {
            return x -> {
                int i = (Integer) x;
                Decimal decimal = new Decimal(i);
                return changePrecision(decimal, target);
            };
        } else if (from instanceof DoubleType) {
            return x -> {
                try {
                    double d = (Double) x;
                    Decimal decimal = new Decimal(d);
                    return changePrecision(decimal, target);
                } catch (NumberFormatException e) {
                    return null;
                }
            };
        } else if (from instanceof FloatType) {
            return x -> {
                try {
                    float f = (Float) x;
                    Decimal decimal = new Decimal(f);
                    return changePrecision(decimal, target);
                } catch (NumberFormatException e) {
                    return null;
                }
            };
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to DecimalType");
        }
    }

    /**
     * Converts to FloatType.
     */
    private Function<Object, Object> castToFloat(DataType from) {
        if (from instanceof StringType) {
            return x -> {
                UTF8String s = (UTF8String) x;
                String floatStr = s.toString();
                try {
                    return Float.parseFloat(floatStr);
                } catch (NumberFormatException e) {
                    Object special = processFloatingPointSpecialLiterals(floatStr, true);
                    return special != null ? special : null;
                }
            };
        } else if (from instanceof BooleanType) {
            return x -> ((Boolean) x) ? 1.0f : 0.0f;
        } else if (from instanceof DateType) {
            return x -> null; // DateType cannot be cast to FloatType
        } else if (from instanceof TimestampType) {
            return x -> {
                double d = timestampToDouble((Long) x);
                return (float) d;
            };
        } else if (from instanceof NumericType) {
            // Handle different numeric types
            if (from instanceof FloatType) {
                return x -> x; // No conversion needed
            } else if (from instanceof DoubleType) {
                return x -> ((Double) x).floatValue();
            } else if (from instanceof LongType) {
                return x -> ((Long) x).floatValue();
            } else if (from instanceof IntegerType) {
                return x -> ((Integer) x).floatValue();
            } else if (from instanceof DecimalType) {
                return x -> ((Decimal) x).toFloat();
            } else {
                throw new UnsupportedOperationException("Cannot cast " + from + " to FloatType");
            }
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to FloatType");
        }
    }

    /**
     * Converts to DateType.
     */
    private Function<Object, Object> castToDate(DataType from) {
        if (from instanceof StringType) {
            return x -> {
                UTF8String s = (UTF8String) x;
                return JippleDateTimeUtils.stringToDate(s).getOrElse(null);
            };
        } else if (from instanceof TimestampType) {
            // According to Hive, throw valid precision more than seconds.
            // Timestamp.nanos is in 0 to 999,999,999, no more than a second.
            return x -> JippleDateTimeUtils.microsToDays((Long) x, zoneId());
        } else if (from instanceof TimestampNTZType) {
            return x -> JippleDateTimeUtils.microsToDays((Long) x, ZoneOffset.UTC);
        } else {
            throw new UnsupportedOperationException("Cannot cast " + from + " to DateType");
        }
    }

    private Function<Object, Object> castToString(DataType from) {
        if (from instanceof StringType) {
            return x -> x;
        } else if (from instanceof BinaryType) {
            return x -> UTF8String.fromBytes((byte[]) x);
        } else if (from instanceof DateType) {
            return x -> UTF8String.fromString(dateFormatter.format((Integer) x));
        } else if (from instanceof TimestampType) {
            return x -> UTF8String.fromString(timestampFormatter.format((Long) x));
        } else if (from instanceof TimestampNTZType) {
            return x -> UTF8String.fromString(timestampNTZFormatter.format((Long) x));
        } else if (from instanceof ArrayType arrayType) {
            final Function<Object, Object> toUTF8String = castToString(arrayType.elementType);
            return x -> {
                ArrayData array = (ArrayData) x;
                UTF8StringBuilder builder = new UTF8StringBuilder();
                builder.append("[");
                int size = array.numElements();
                if (size > 0) {
                    if (array.isNullAt(0)) {
                        if (!nullString.isEmpty()) {
                            builder.append(nullString);
                        }
                    } else {
                        builder.append((UTF8String) toUTF8String.apply(array.get(0, arrayType.elementType)));
                    }
                    for (int i = 1; i < size; i++) {
                        builder.append(",");
                        if (array.isNullAt(i)) {
                            if (!nullString.isEmpty()) {
                                builder.append(" " + nullString);
                            }
                        } else {
                            builder.append(" ");
                            builder.append((UTF8String) toUTF8String.apply(array.get(i, arrayType.elementType)));
                        }
                    }
                }
                builder.append("]");
                return builder.build();
            };
        } else if (from instanceof MapType mapType) {
            final Function<Object, Object> keyToUTF8String = castToString(mapType.keyType);
            final Function<Object, Object> valueToUTF8String = castToString(mapType.valueType);
            return x -> {
                MapData map = (MapData) x;
                UTF8StringBuilder builder = new UTF8StringBuilder();
                builder.append(leftBracket);
                int size = map.numElements();
                if (size > 0) {
                    ArrayData keyArray = map.keyArray();
                    ArrayData valueArray = map.valueArray();
                    builder.append((UTF8String) keyToUTF8String.apply(keyArray.get(0, mapType.keyType)));
                    builder.append(" ->");
                    if (valueArray.isNullAt(0)) {
                        if (!nullString.isEmpty()) {
                            builder.append(" " + nullString);
                        }
                    } else {
                        builder.append(" ");
                        builder.append((UTF8String) valueToUTF8String.apply(valueArray.get(0, mapType.valueType)));
                    }
                    for (int i = 1; i < size; i++) {
                        builder.append(", ");
                        builder.append((UTF8String) keyToUTF8String.apply(keyArray.get(i, mapType.keyType)));
                        builder.append(" ->");
                        if (valueArray.isNullAt(i)) {
                            if (!nullString.isEmpty()) {
                                builder.append(" " + nullString);
                            }
                        } else {
                            builder.append(" ");
                            builder.append((UTF8String) valueToUTF8String.apply(valueArray.get(i, mapType.valueType)));
                        }
                    }
                }
                builder.append(rightBracket);
                return builder.build();
            };
        } else if (from instanceof StructType structType) {
            final StructField[] fields = structType.fields;
            final DataType[] dataTypes = new DataType[fields.length];
            @SuppressWarnings("unchecked")
            final Function<Object, Object>[] toUTF8StringFuncs = (Function<Object, Object>[]) new Function[fields.length];
            for (int i = 0; i < fields.length; i++) {
                dataTypes[i] = fields[i].dataType;
                toUTF8StringFuncs[i] = castToString(dataTypes[i]);
            }
            return x -> {
                InternalRow row = (InternalRow) x;
                UTF8StringBuilder builder = new UTF8StringBuilder();
                builder.append(leftBracket);
                int size = row.numFields();
                if (size > 0) {
                    if (row.isNullAt(0)) {
                        if (!nullString.isEmpty()) {
                            builder.append(nullString);
                        }
                    } else {
                        builder.append((UTF8String) toUTF8StringFuncs[0].apply(row.get(0, dataTypes[0])));
                    }
                    for (int i = 1; i < size; i++) {
                        builder.append(",");
                        if (row.isNullAt(i)) {
                            if (!nullString.isEmpty()) {
                                builder.append(" " + nullString);
                            }
                        } else {
                            builder.append(" ");
                            builder.append((UTF8String) toUTF8StringFuncs[i].apply(row.get(i, dataTypes[i])));
                        }
                    }
                }
                builder.append(rightBracket);
                return builder.build();
            };
        }
        else {
            return x -> UTF8String.fromString(x.toString());
        }
    }


    /**
     * Casts an array from one element type to another.
     */
    private Function<Object, Object> castArray(DataType fromType, DataType toType) {
        Function<Object, Object> elementCast = castInternal(fromType, toType);
        return x -> {
            ArrayData array = (ArrayData) x;
            Object[] values = new Object[array.numElements()];
            for (int i = 0; i < array.numElements(); i++) {
                if (array.isNullAt(i)) {
                    values[i] = null;
                } else {
                    Object element = array.get(i, fromType);
                    values[i] = elementCast.apply(element);
                }
            }
            return new GenericArrayData(values);
        };
    }

    /**
     * Casts a map from one key/value type to another.
     */
    private Function<Object, Object> castMap(MapType from, MapType to) {
        Function<Object, Object> keyCast = castArray(from.keyType, to.keyType);
        Function<Object, Object> valueCast = castArray(from.valueType, to.valueType);
        return x -> {
            MapData map = (MapData) x;
            ArrayData keys = (ArrayData) keyCast.apply(map.keyArray());
            ArrayData values = (ArrayData) valueCast.apply(map.valueArray());
            return new ArrayBasedMapData(keys, values);
        };
    }

    /**
     * Casts a struct from one field type to another.
     */
    @SuppressWarnings("unchecked")
    private Function<Object, Object> castStruct(StructType from, StructType to) {
        Function<Object, Object>[] castFuncs = (Function<Object, Object>[]) new Function[from.fields.length];
        for (int i = 0; i < from.fields.length; i++) {
            StructField fromField = from.fields[i];
            StructField toField = to.fields[i];
            castFuncs[i] = castInternal(fromField.dataType, toField.dataType);
        }
        return x -> {
            InternalRow row = (InternalRow) x;
            GenericInternalRow newRow = new GenericInternalRow(from.fields.length);
            for (int i = 0; i < row.numFields(); i++) {
                if (row.isNullAt(i)) {
                    newRow.update(i, null);
                } else {
                    Object value = row.get(i, from.fields[i].dataType);
                    newRow.update(i, castFuncs[i].apply(value));
                }
            }
            return newRow;
        };
    }
    private Function<Object, Object> castInternal(DataType from, DataType to) {
        // If the cast does not change the structure, then we don't really need to cast anything.
        // We can return what the children return. Same thing should happen in the codegen path.
        if (DataType.equalsStructurally(from, to)) {
            return x -> x;
        } else if (from instanceof NullType) {
            // According to `canCast`, NullType can be casted to any type.
            // For primitive types, we don't reach here because the guard of `nullSafeEval`.
            // But for nested types like struct, we might reach here for nested null type field.
            // We won't call the returned function actually, but returns a placeholder.
            throw QueryExecutionErrors.cannotCastFromNullTypeError(to);
        } else {
            if (from.equals(to)) {
                return x -> x;
            } else if (to instanceof StringType) {
                return castToString(from);
            } else if (to instanceof BooleanType) {
                return castToBoolean(from);
            } else if (to instanceof BinaryType) {
                return castToBinary(from);
            } else if (to instanceof TimestampType) {
                return castToTimestamp(from);
            } else if (to instanceof TimestampNTZType) {
                return castToTimestampNTZ(from);
            } else if (to instanceof DateType) {
                return castToDate(from);
            } else if (to instanceof LongType) {
                return castToLong(from);
            } else if (to instanceof IntegerType) {
                return castToInt(from);
            } else if (to instanceof DoubleType) {
                return castToDouble(from);
            } else if (to instanceof FloatType) {
                return castToFloat(from);
            } else if (to instanceof DecimalType) {
                return castToDecimal(from, (DecimalType) to);
            } else if (to instanceof ArrayType toArray) {
                if (from instanceof ArrayType fromArray) {
                    return castArray(fromArray.elementType, toArray.elementType);
                } else {
                    throw new UnsupportedOperationException("Cannot cast " + from + " to " + to);
                }
            } else if (to instanceof MapType toMap) {
                if (from instanceof MapType fromMap) {
                    return castMap(fromMap, toMap);
                } else {
                    throw new UnsupportedOperationException("Cannot cast " + from + " to " + to);
                }
            } else if (to instanceof StructType toStruct) {
                if (from instanceof StructType fromStruct) {
                    return castStruct(fromStruct, toStruct);
                } else {
                    throw new UnsupportedOperationException("Cannot cast " + from + " to " + to);
                }
            } else {
                throw new UnsupportedOperationException("Cannot cast " + from + " to " + to);
            }
        }
    }

    public Function<Object, Object> cast() {
        if (_cast == null) {
            _cast = castInternal(child.dataType(), dataType);
            dateFormatter = DateFormatter.getFormatter();
            if (needsTimeZone()) {
                timestampFormatter = TimestampFormatter.getFormatter(zoneId());
            }
            timestampNTZFormatter = TimestampFormatter.getFormatter(ZoneOffset.UTC);
        }
        return _cast;
    }

    @Override
    protected Object nullSafeEval(Object input) {
        return cast().apply(input);
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new Cast(newChild, dataType, timeZoneId);
    }

    @Override
    public String toString() {
        return prettyName() + "(" + child + " as " + dataType.simpleString() + ")";
    }

    @Override
    public String sql() {
        return prettyName().toUpperCase() + "(" + child.sql() + " AS " + dataType.sql() + ")";
    }

    /**
     * Returns true iff we can cast `from` type to `to` type.
     */
    public static boolean canCast(DataType from, DataType to) {
        // Same type
        if (from.equals(to)) {
            return true;
        }

        // NullType can be cast to any type
        if (from instanceof NullType) {
            return true;
        }

        // Any type can be cast to StringType
        if (to instanceof StringType) {
            return true;
        }

        // StringType to BinaryType
        if (from instanceof StringType && to instanceof BinaryType) {
            return true;
        }
        // IntegralType to BinaryType
        if (from instanceof IntegralType && to instanceof BinaryType) {
            return true;
        }

        // StringType to BooleanType
        if (from instanceof StringType && to instanceof BooleanType) {
            return true;
        }
        // DateType to BooleanType
        if (from instanceof DateType && to instanceof BooleanType) {
            return true;
        }
        // TimestampType to BooleanType
        if (from instanceof TimestampType && to instanceof BooleanType) {
            return true;
        }
        // NumericType to BooleanType
        if (from instanceof NumericType && to instanceof BooleanType) {
            return true;
        }

        // StringType to TimestampType
        if (from instanceof StringType && to instanceof TimestampType) {
            return true;
        }
        // BooleanType to TimestampType
        if (from instanceof BooleanType && to instanceof TimestampType) {
            return true;
        }
        // DateType to TimestampType
        if (from instanceof DateType && to instanceof TimestampType) {
            return true;
        }
        // NumericType to TimestampType
        if (from instanceof NumericType && to instanceof TimestampType) {
            return true;
        }
        // TimestampNTZType to TimestampType
        if (from instanceof TimestampNTZType && to instanceof TimestampType) {
            return true;
        }

        // StringType to TimestampNTZType
        if (from instanceof StringType && to instanceof TimestampNTZType) {
            return true;
        }
        // DateType to TimestampNTZType
        if (from instanceof DateType && to instanceof TimestampNTZType) {
            return true;
        }
        // TimestampType to TimestampNTZType
        if (from instanceof TimestampType && to instanceof TimestampNTZType) {
            return true;
        }

        // StringType to DateType
        if (from instanceof StringType && to instanceof DateType) {
            return true;
        }
        // TimestampType to DateType
        if (from instanceof TimestampType && to instanceof DateType) {
            return true;
        }
        // TimestampNTZType to DateType
        if (from instanceof TimestampNTZType && to instanceof DateType) {
            return true;
        }

        // StringType to CalendarIntervalType
        if (from instanceof StringType && to instanceof CalendarIntervalType) {
            return true;
        }


        // StringType to NumericType
        if (from instanceof StringType && to instanceof NumericType) {
            return true;
        }
        // BooleanType to NumericType
        if (from instanceof BooleanType && to instanceof NumericType) {
            return true;
        }
        // DateType to NumericType
        if (from instanceof DateType && to instanceof NumericType) {
            return true;
        }
        // TimestampType to NumericType
        if (from instanceof TimestampType && to instanceof NumericType) {
            return true;
        }
        // NumericType to NumericType
        if (from instanceof NumericType && to instanceof NumericType) {
            return true;
        }

        // ArrayType to ArrayType
        if (from instanceof ArrayType fromArray && to instanceof ArrayType toArray) {
            return canCast(fromArray.elementType, toArray.elementType) &&
                    resolvableNullability(
                            fromArray.containsNull || forceNullable(fromArray.elementType, toArray.elementType),
                            toArray.containsNull);
        }

        // MapType to MapType
        if (from instanceof MapType fromMap && to instanceof MapType toMap) {
            return canCast(fromMap.keyType, toMap.keyType) &&
                    !forceNullable(fromMap.keyType, toMap.keyType) &&
                    canCast(fromMap.valueType, toMap.valueType) &&
                    resolvableNullability(
                            fromMap.valueContainsNull || forceNullable(fromMap.valueType, toMap.valueType),
                            toMap.valueContainsNull);
        }

        // StructType to StructType
        if (from instanceof StructType fromStruct && to instanceof StructType toStruct) {
            if (fromStruct.fields.length != toStruct.fields.length) {
                return false;
            }
            for (int i = 0; i < fromStruct.fields.length; i++) {
                StructField fromField = fromStruct.fields[i];
                StructField toField = toStruct.fields[i];
                
                if (!canCast(fromField.dataType, toField.dataType)) {
                    return false;
                }
                if (!resolvableNullability(
                        fromField.nullable || forceNullable(fromField.dataType, toField.dataType),
                        toField.nullable)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    /**
     * Return true if we need to use the `timeZone` information casting `from` type to `to` type.
     * The patterns matched reflect the current implementation in the Cast node.
     * c.f. usage of `timeZone` in:
     * * Cast.castToString
     * * Cast.castToDate
     * * Cast.castToTimestamp
     */
    public static boolean needsTimeZone(DataType from, DataType to) {
        // StringType to TimestampType or DateType
        if (from instanceof StringType && (to instanceof TimestampType || to instanceof DateType)) {
            return true;
        }
        // TimestampType or DateType to StringType
        if ((from instanceof TimestampType || from instanceof DateType) && to instanceof StringType) {
            return true;
        }
        // DateType to TimestampType
        if (from instanceof DateType && to instanceof TimestampType) {
            return true;
        }
        // TimestampType to DateType
        if (from instanceof TimestampType && to instanceof DateType) {
            return true;
        }
        // TimestampType to TimestampNTZType
        if (from instanceof TimestampType && to instanceof TimestampNTZType) {
            return true;
        }
        // TimestampNTZType to TimestampType
        if (from instanceof TimestampNTZType && to instanceof TimestampType) {
            return true;
        }

        // ArrayType to ArrayType
        if (from instanceof ArrayType fromArray && to instanceof ArrayType toArray) {
            return needsTimeZone(fromArray.elementType, toArray.elementType);
        }

        // MapType to MapType
        if (from instanceof MapType fromMap && to instanceof MapType toMap) {
            return needsTimeZone(fromMap.keyType, toMap.keyType) ||
                   needsTimeZone(fromMap.valueType, toMap.valueType);
        }

        // StructType to StructType
        if (from instanceof StructType fromStruct && to instanceof StructType toStruct) {
            if (fromStruct.fields.length != toStruct.fields.length) {
                return false;
            }
            for (int i = 0; i < fromStruct.fields.length; i++) {
                if (needsTimeZone(fromStruct.fields[i].dataType, toStruct.fields[i].dataType)) {
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    /**
     * Checks if casting from `from` type to `to` DecimalType is null-safe.
     * Returns true if the cast is guaranteed to not produce null for non-null inputs.
     */
    private static boolean canNullSafeCastToDecimal(DataType from, DecimalType to) {
        if (from instanceof BooleanType) {
            return to.isWiderThan(DecimalType.BooleanDecimal);
        } else if (from instanceof NumericType) {
            return to.isWiderThan(from);
        } else if (from instanceof DecimalType fromDecimal) {
            // truncating or precision lose
            return (to.precision - to.scale) > (fromDecimal.precision - fromDecimal.scale);
        } else {
            return false; // overflow
        }
    }

    /**
     * Returns `true` if casting non-nullable values from `from` type to `to` type
     * may return null. Note that the caller side should take care of input nullability
     * first and only call this method if the input is not nullable.
     */
    public static boolean forceNullable(DataType from, DataType to) {
        // NullType to any type - empty array or map case
        if (from instanceof NullType) {
            return false;
        }
        
        // Same type
        if (from.equals(to)) {
            return false;
        }

        // StringType to BinaryType
        if (from instanceof StringType && to instanceof BinaryType) {
            return false;
        }
        // StringType to any type (except BinaryType)
        if (from instanceof StringType) {
            return true;
        }
        // Any type to StringType
        if (to instanceof StringType) {
            return false;
        }

        // FloatType or DoubleType to TimestampType
        if ((from instanceof FloatType || from instanceof DoubleType) && to instanceof TimestampType) {
            return true;
        }
        // TimestampType to DateType
        if (from instanceof TimestampType && to instanceof DateType) {
            return false;
        }
        // Any type to DateType (except TimestampType)
        if (to instanceof DateType && !(from instanceof TimestampType)) {
            return true;
        }
        // DateType to TimestampType
        if (from instanceof DateType && to instanceof TimestampType) {
            return false;
        }
        // DateType to any type (except TimestampType)
        if (from instanceof DateType && !(to instanceof TimestampType)) {
            return true;
        }
        // Any type to CalendarIntervalType
        if (to instanceof CalendarIntervalType) {
            return true;
        }

        // Any type to DecimalType (if not null-safe cast)
        if (to instanceof DecimalType toDecimal) {
            if (!canNullSafeCastToDecimal(from, toDecimal)) {
                return true;
            }
        }
        // FractionalType to IntegralType - NaN, infinity
        if (from instanceof FractionalType && to instanceof IntegralType) {
            return true;
        }
        
        return false;
    }

    /**
     * Checks if nullability can be resolved from `from` to `to`.
     * Returns true if `from` nullable can be resolved to `to` nullable.
     * Typically, this means if `from` is nullable, `to` must also be nullable.
     */
    private static boolean resolvableNullability(boolean from, boolean to) {
        return !from || to;
    }
}
