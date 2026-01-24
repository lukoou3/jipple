package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.EmptyBlock;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.ExprValue;
import com.jipple.sql.catalyst.expressions.codegen.FalseLiteral;
import com.jipple.sql.catalyst.expressions.codegen.Inline;
import com.jipple.sql.catalyst.expressions.codegen.JavaCode;
import com.jipple.sql.catalyst.util.*;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.sql.types.*;
import com.jipple.unsafe.UTF8StringBuilder;
import com.jipple.unsafe.types.UTF8String;

import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                        builder.append(nullString);
                    } else {
                        builder.append((UTF8String) toUTF8String.apply(array.get(0, arrayType.elementType)));
                    }
                    for (int i = 1; i < size; i++) {
                        builder.append(",");
                        if (array.isNullAt(i)) {
                            builder.append(" " + nullString);
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
                        builder.append(" " + nullString);
                    } else {
                        builder.append(" ");
                        builder.append((UTF8String) valueToUTF8String.apply(valueArray.get(0, mapType.valueType)));
                    }
                    for (int i = 1; i < size; i++) {
                        builder.append(", ");
                        builder.append((UTF8String) keyToUTF8String.apply(keyArray.get(i, mapType.keyType)));
                        builder.append(" ->");
                        if (valueArray.isNullAt(i)) {
                            builder.append(" " + nullString);
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
                        builder.append(nullString);
                    } else {
                        builder.append((UTF8String) toUTF8StringFuncs[0].apply(row.get(0, dataTypes[0])));
                    }
                    for (int i = 1; i < size; i++) {
                        builder.append(",");
                        if (row.isNullAt(i)) {
                            builder.append(" " + nullString);
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
    public ExprCode genCode(CodegenContext ctx) {
        // If the cast does not change the structure, then we don't really need to cast anything.
        // We can return what the children return. Same thing should happen in the interpreted path.
        if (DataType.equalsStructurally(child.dataType(), dataType)) {
            return child.genCode(ctx);
        }
        return super.genCode(ctx);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        ExprCode eval = child.genCode(ctx);
        CastFunction nullSafeCast = nullSafeCastFunction(child.dataType(), dataType, ctx);
        Block code = castCode(ctx, eval.value, eval.isNull, ev.value, ev.isNull, dataType, nullSafeCast);
        return ev.copy(eval.code.plus(code));
    }

    @FunctionalInterface
    private interface CastFunction {
        Block apply(ExprValue input, ExprValue result, ExprValue resultIsNull);
    }

    @FunctionalInterface
    private interface ToStringCode {
        Block apply(ExprValue input, ExprValue result);
    }

    private Block castCode(
            CodegenContext ctx,
            ExprValue input,
            ExprValue inputIsNull,
            ExprValue result,
            ExprValue resultIsNull,
            DataType resultType,
            CastFunction cast) {
        String javaType = CodeGeneratorUtils.javaType(resultType);
        Block castBody = cast.apply(input, result, resultIsNull);
        return Block.block(
                """
                        boolean ${resultIsNull} = ${inputIsNull};
                        ${javaType} ${result} = ${defaultValue};
                        if (!${inputIsNull}) {
                          ${castBody}
                        }
                        """,
                Map.ofEntries(
                        Map.entry("resultIsNull", resultIsNull),
                        Map.entry("inputIsNull", inputIsNull),
                        Map.entry("javaType", javaType),
                        Map.entry("result", result),
                        Map.entry("defaultValue", CodeGeneratorUtils.defaultValue(resultType)),
                        Map.entry("castBody", castBody)
                )
        );
    }

    private CastFunction nullSafeCastFunction(
            DataType from,
            DataType to,
            CodegenContext ctx) {
        if (from instanceof NullType) {
            return (c, evPrim, evNull) -> Block.block(
                    "${evNull} = true;",
                    Map.ofEntries(
                            Map.entry("evNull", evNull)
                    )
            );
        }
        if (from.equals(to)) {
            return (c, evPrim, evNull) -> Block.block(
                    "${evPrim} = ${input};",
                    Map.ofEntries(
                            Map.entry("evPrim", evPrim),
                            Map.entry("input", c)
                    )
            );
        }
        CastFunction cast = null;
        if (to instanceof StringType) {
            cast = castToStringCode(from, ctx);
        } else if (to instanceof BinaryType) {
            cast = castToBinaryCode(from);
        } else if (to instanceof DateType) {
            cast = castToDateCode(from, ctx);
        } else if (to instanceof DecimalType decimalType) {
            cast = castToDecimalCode(from, decimalType, ctx);
        } else if (to instanceof TimestampType) {
            cast = castToTimestampCode(from, ctx);
        } else if (to instanceof TimestampNTZType) {
            cast = castToTimestampNTZCode(from, ctx);
        } else if (to instanceof BooleanType) {
            cast = castToBooleanCode(from, ctx);
        } else if (to instanceof IntegerType) {
            cast = castToIntCode(from, ctx);
        } else if (to instanceof FloatType) {
            cast = castToFloatCode(from, ctx);
        } else if (to instanceof LongType) {
            cast = castToLongCode(from, ctx);
        } else if (to instanceof DoubleType) {
            cast = castToDoubleCode(from, ctx);
        } else if (to instanceof ArrayType array && from instanceof ArrayType fromArray) {
            cast = castArrayCode(fromArray.elementType, array.elementType, ctx);
        } else if (to instanceof MapType map && from instanceof MapType fromMap) {
            cast = castMapCode(fromMap, map, ctx);
        } else if (to instanceof StructType struct && from instanceof StructType fromStruct) {
            cast = castStructCode(fromStruct, struct, ctx);
        }
        if (cast != null) {
            return cast;
        }
        throw new UnsupportedOperationException("Cannot cast " + from + " to " + to);
    }

    private CastFunction castToStringCode(DataType from, CodegenContext ctx) {
        ToStringCode toStringCode = castToStringCodeInternal(from, ctx);
        return (c, evPrim, evNull) -> toStringCode.apply(c, evPrim);
    }

    private ToStringCode castToStringCodeInternal(DataType from, CodegenContext ctx) {
        String utf8StringClass = UTF8String.class.getName();
        if (from instanceof StringType) {
            return (c, evPrim) -> Block.block(
                    "${value} = (${utf8String}) ${input};",
                    Map.ofEntries(
                            Map.entry("value", evPrim),
                            Map.entry("utf8String", utf8StringClass),
                            Map.entry("input", c)
                    )
            );
        }
        if (from instanceof BinaryType) {
            return (c, evPrim) -> Block.block(
                    "${value} = ${utf8String}.fromBytes((byte[]) ${input});",
                    Map.ofEntries(
                            Map.entry("value", evPrim),
                            Map.entry("utf8String", utf8StringClass),
                            Map.entry("input", c)
                    )
            );
        }
        if (from instanceof DateType) {
            String formatter = ctx.addReferenceObj("dateFormatter",
                    DateFormatter.getFormatter(),
                    DateFormatter.class.getName());
            return (c, evPrim) -> Block.block(
                    "${value} = ${utf8String}.fromString(${formatter}.format((Integer) ${input}));",
                    Map.ofEntries(
                            Map.entry("value", evPrim),
                            Map.entry("utf8String", utf8StringClass),
                            Map.entry("formatter", formatter),
                            Map.entry("input", c)
                    )
            );
        }
        if (from instanceof TimestampType) {
            ZoneId zid = zoneId();
            String formatter = ctx.addReferenceObj(
                    "timestampFormatter",
                    TimestampFormatter.getFormatter(zid),
                    TimestampFormatter.class.getName());
            return (c, evPrim) -> Block.block(
                    "${value} = ${utf8String}.fromString(${formatter}.format((Long) ${input}));",
                    Map.ofEntries(
                            Map.entry("value", evPrim),
                            Map.entry("utf8String", utf8StringClass),
                            Map.entry("formatter", formatter),
                            Map.entry("input", c)
                    )
            );
        }
        if (from instanceof TimestampNTZType) {
            String formatter = ctx.addReferenceObj(
                    "timestampNTZFormatter",
                    TimestampFormatter.getFormatter(ZoneOffset.UTC),
                    TimestampFormatter.class.getName());
            return (c, evPrim) -> Block.block(
                    "${value} = ${utf8String}.fromString(${formatter}.format((Long) ${input}));",
                    Map.ofEntries(
                            Map.entry("value", evPrim),
                            Map.entry("utf8String", utf8StringClass),
                            Map.entry("formatter", formatter),
                            Map.entry("input", c)
                    )
            );
        }
        if (from instanceof ArrayType arrayType) {
            return (c, evPrim) -> {
                ExprValue buffer = ctx.freshVariable("buffer", UTF8StringBuilder.class);
                Inline bufferClass = JavaCode.javaType(UTF8StringBuilder.class);
                Block writeArrayElemCode = writeArrayToStringBuilder(arrayType.elementType, c, buffer, ctx);
                return Block.block(
                        """
                                ${bufferClass} ${buffer} = new ${bufferClass}();
                                ${writeArrayElemCode};
                                ${value} = ${buffer}.build();
                                """,
                        Map.ofEntries(
                                Map.entry("bufferClass", bufferClass),
                                Map.entry("buffer", buffer),
                                Map.entry("writeArrayElemCode", writeArrayElemCode),
                                Map.entry("value", evPrim)
                        )
                );
            };
        }
        if (from instanceof MapType mapType) {
            return (c, evPrim) -> {
                ExprValue buffer = ctx.freshVariable("buffer", UTF8StringBuilder.class);
                Inline bufferClass = JavaCode.javaType(UTF8StringBuilder.class);
                Block writeMapElemCode = writeMapToStringBuilder(mapType.keyType, mapType.valueType, c, buffer, ctx);
                return Block.block(
                        """
                                ${bufferClass} ${buffer} = new ${bufferClass}();
                                ${writeMapElemCode};
                                ${value} = ${buffer}.build();
                                """,
                        Map.ofEntries(
                                Map.entry("bufferClass", bufferClass),
                                Map.entry("buffer", buffer),
                                Map.entry("writeMapElemCode", writeMapElemCode),
                                Map.entry("value", evPrim)
                        )
                );
            };
        }
        if (from instanceof StructType structType) {
            return (c, evPrim) -> {
                ExprValue row = ctx.freshVariable("row", InternalRow.class);
                ExprValue buffer = ctx.freshVariable("buffer", UTF8StringBuilder.class);
                Inline bufferClass = JavaCode.javaType(UTF8StringBuilder.class);
                DataType[] fieldTypes = new DataType[structType.fields.length];
                for (int i = 0; i < structType.fields.length; i++) {
                    fieldTypes[i] = structType.fields[i].dataType;
                }
                Block writeStructCode = writeStructToStringBuilder(fieldTypes, row, buffer, ctx);
                return Block.block(
                        """
                                InternalRow ${row} = ${input};
                                ${bufferClass} ${buffer} = new ${bufferClass}();
                                ${writeStructCode}
                                ${value} = ${buffer}.build();
                                """,
                        Map.ofEntries(
                                Map.entry("row", row),
                                Map.entry("input", c),
                                Map.entry("bufferClass", bufferClass),
                                Map.entry("buffer", buffer),
                                Map.entry("writeStructCode", writeStructCode),
                                Map.entry("value", evPrim)
                        )
                );
            };
        }
        if (from instanceof DecimalType) {
            return (c, evPrim) -> Block.block(
                    "${value} = ${utf8String}.fromString(String.valueOf(${input}));",
                    Map.ofEntries(
                            Map.entry("value", evPrim),
                            Map.entry("utf8String", utf8StringClass),
                            Map.entry("input", c)
                    )
            );
        }
        if (from instanceof BooleanType || isSimpleNumeric(from)) {
            return (c, evPrim) -> Block.block(
                    "${value} = ${utf8String}.fromString(String.valueOf(${input}));",
                    Map.ofEntries(
                            Map.entry("value", evPrim),
                            Map.entry("utf8String", utf8StringClass),
                            Map.entry("input", c)
                    )
            );
        }
        throw new UnsupportedOperationException("Cannot cast " + from + " to string");
    }

    private Block appendNull(ExprValue buffer, boolean isFirstElement) {
        if (Cast.nullString.isEmpty()) {
            return EmptyBlock.INSTANCE;
        }
        String literal = isFirstElement ? Cast.nullString : " " + Cast.nullString;
        return Block.block(
                "${buffer}.append(\"${literal}\");",
                Map.ofEntries(
                        Map.entry("buffer", buffer),
                        Map.entry("literal", literal)
                )
        );
    }

    private Block writeArrayToStringBuilder(
            DataType elementType,
            ExprValue array,
            ExprValue buffer,
            CodegenContext ctx) {
        ToStringCode elementToStringCode = castToStringCodeInternal(elementType, ctx);
        String funcName = ctx.freshName("elementToString");
        ExprValue element = JavaCode.variable("element", elementType);
        ExprValue elementStr = JavaCode.variable("elementStr", StringType.INSTANCE);
        String elementJavaType = CodeGeneratorUtils.javaType(elementType);
        Inline elementToStringFunc = new Inline(
                ctx.addNewFunction(
                        funcName,
                        CodeGeneratorUtils.template(
                                """
                                        private UTF8String ${funcName}(${elementJavaType} ${element}) {
                                          UTF8String ${elementStr} = null;
                                          ${elementToStringCode}
                                          return ${elementStr};
                                        }
                                        """,
                                Map.ofEntries(
                                        Map.entry("funcName", funcName),
                                        Map.entry("elementJavaType", elementJavaType),
                                        Map.entry("element", element),
                                        Map.entry("elementStr", elementStr),
                                        Map.entry("elementToStringCode", elementToStringCode.apply(element, elementStr))
                                )
                        )
                )
        );
        ExprValue loopIndex = ctx.freshVariable("loopIndex", IntegerType.INSTANCE);
        String getFirst = CodeGeneratorUtils.getValue(array.toString(), elementType, "0");
        String getLoop = CodeGeneratorUtils.getValue(array.toString(), elementType, loopIndex.toString());
        return Block.block(
                """
                        ${buffer}.append("[");
                        if (${array}.numElements() > 0) {
                          if (${array}.isNullAt(0)) {
                            ${appendNullFirst}
                          } else {
                            ${buffer}.append(${elementToStringFunc}(${getFirst}));
                          }
                          for (int ${loopIndex} = 1; ${loopIndex} < ${array}.numElements(); ${loopIndex}++) {
                            ${buffer}.append(",");
                            if (${array}.isNullAt(${loopIndex})) {
                              ${appendNull}
                            } else {
                              ${buffer}.append(" ");
                              ${buffer}.append(${elementToStringFunc}(${getLoop}));
                            }
                          }
                        }
                        ${buffer}.append("]");
                        """,
                Map.ofEntries(
                        Map.entry("buffer", buffer),
                        Map.entry("array", array),
                        Map.entry("appendNullFirst", appendNull(buffer, true)),
                        Map.entry("appendNull", appendNull(buffer, false)),
                        Map.entry("elementToStringFunc", elementToStringFunc),
                        Map.entry("getFirst", getFirst),
                        Map.entry("loopIndex", loopIndex),
                        Map.entry("getLoop", getLoop)
                )
        );
    }

    private Block writeMapToStringBuilder(
            DataType keyType,
            DataType valueType,
            ExprValue map,
            ExprValue buffer,
            CodegenContext ctx) {
        Inline keyToStringFunc = dataToStringFunc("keyToString", keyType, ctx);
        Inline valueToStringFunc = dataToStringFunc("valueToString", valueType, ctx);
        ExprValue loopIndex = ctx.freshVariable("loopIndex", IntegerType.INSTANCE);
        String mapKeyArray = map + ".keyArray()";
        String mapValueArray = map + ".valueArray()";
        String getMapFirstKey = CodeGeneratorUtils.getValue(mapKeyArray, keyType, "0");
        String getMapFirstValue = CodeGeneratorUtils.getValue(mapValueArray, valueType, "0");
        String getMapKeyArray = CodeGeneratorUtils.getValue(mapKeyArray, keyType, loopIndex.toString());
        String getMapValueArray = CodeGeneratorUtils.getValue(mapValueArray, valueType, loopIndex.toString());
        return Block.block(
                """
                        ${buffer}.append("${leftBracket}");
                        if (${map}.numElements() > 0) {
                          ${buffer}.append(${keyToStringFunc}(${getMapFirstKey}));
                          ${buffer}.append(" ->");
                          if (${map}.valueArray().isNullAt(0)) {
                            ${appendNull}
                          } else {
                            ${buffer}.append(" ");
                            ${buffer}.append(${valueToStringFunc}(${getMapFirstValue}));
                          }
                          for (int ${loopIndex} = 1; ${loopIndex} < ${map}.numElements(); ${loopIndex}++) {
                            ${buffer}.append(", ");
                            ${buffer}.append(${keyToStringFunc}(${getMapKeyArray}));
                            ${buffer}.append(" ->");
                            if (${map}.valueArray().isNullAt(${loopIndex})) {
                              ${appendNull}
                            } else {
                              ${buffer}.append(" ");
                              ${buffer}.append(${valueToStringFunc}(${getMapValueArray}));
                            }
                          }
                        }
                        ${buffer}.append("${rightBracket}");
                        """,
                Map.ofEntries(
                        Map.entry("buffer", buffer),
                        Map.entry("map", map),
                        Map.entry("leftBracket", Cast.leftBracket),
                        Map.entry("rightBracket", Cast.rightBracket),
                        Map.entry("appendNull", appendNull(buffer, false)),
                        Map.entry("keyToStringFunc", keyToStringFunc),
                        Map.entry("valueToStringFunc", valueToStringFunc),
                        Map.entry("getMapFirstKey", getMapFirstKey),
                        Map.entry("getMapFirstValue", getMapFirstValue),
                        Map.entry("loopIndex", loopIndex),
                        Map.entry("getMapKeyArray", getMapKeyArray),
                        Map.entry("getMapValueArray", getMapValueArray)
                )
        );
    }

    private Inline dataToStringFunc(String funcBaseName, DataType dataType, CodegenContext ctx) {
        String funcName = ctx.freshName(funcBaseName);
        ToStringCode dataToStringCode = castToStringCodeInternal(dataType, ctx);
        ExprValue data = JavaCode.variable("data", dataType);
        ExprValue dataStr = JavaCode.variable("dataStr", StringType.INSTANCE);
        String javaType = CodeGeneratorUtils.javaType(dataType);
        return new Inline(
                ctx.addNewFunction(
                        funcName,
                        CodeGeneratorUtils.template(
                                """
                                        private UTF8String ${funcName}(${javaType} ${data}) {
                                          UTF8String ${dataStr} = null;
                                          ${dataToStringCode}
                                          return ${dataStr};
                                        }
                                        """,
                                Map.ofEntries(
                                        Map.entry("funcName", funcName),
                                        Map.entry("javaType", javaType),
                                        Map.entry("data", data),
                                        Map.entry("dataStr", dataStr),
                                        Map.entry("dataToStringCode", dataToStringCode.apply(data, dataStr))
                                )
                        )
                )
        );
    }

    private Block writeStructToStringBuilder(
            DataType[] fieldTypes,
            ExprValue row,
            ExprValue buffer,
            CodegenContext ctx) {
        List<String> structToStringCode = new ArrayList<>(fieldTypes.length);
        for (int i = 0; i < fieldTypes.length; i++) {
            DataType fieldType = fieldTypes[i];
            ToStringCode fieldToStringCode = castToStringCodeInternal(fieldType, ctx);
            ExprValue field = ctx.freshVariable("field", fieldType);
            ExprValue fieldStr = ctx.freshVariable("fieldStr", StringType.INSTANCE);
            String javaType = CodeGeneratorUtils.javaType(fieldType);
            String getValue = CodeGeneratorUtils.getValue(row.toString(), fieldType, String.valueOf(i));
            Block appendComma = (i == 0)
                    ? EmptyBlock.INSTANCE
                    : Block.block(
                            "${buffer}.append(\",\");",
                            Map.of("buffer", buffer)
                    );
            Block appendSpace = (i == 0)
                    ? EmptyBlock.INSTANCE
                    : Block.block(
                            "${buffer}.append(\" \");",
                            Map.of("buffer", buffer)
                    );
            Block code = Block.block(
                    """
                            ${appendComma}
                            if (${row}.isNullAt(${index})) {
                              ${appendNull}
                            } else {
                              ${appendSpace}
                              ${javaType} ${field} = ${getValue};
                              UTF8String ${fieldStr} = null;
                              ${fieldToStringCode}
                              ${buffer}.append(${fieldStr});
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("appendComma", appendComma),
                            Map.entry("row", row),
                            Map.entry("index", i),
                            Map.entry("appendNull", appendNull(buffer, i == 0)),
                            Map.entry("appendSpace", appendSpace),
                            Map.entry("javaType", javaType),
                            Map.entry("field", field),
                            Map.entry("getValue", getValue),
                            Map.entry("fieldStr", fieldStr),
                            Map.entry("fieldToStringCode", fieldToStringCode.apply(field, fieldStr)),
                            Map.entry("buffer", buffer)
                    )
            );
            structToStringCode.add(code.toString());
        }
        String writeStructCode = ctx.splitExpressions(
                structToStringCode,
                "fieldToString",
                List.of(
                        com.jipple.tuple.Tuple2.of("InternalRow", row.toString()),
                        com.jipple.tuple.Tuple2.of(UTF8StringBuilder.class.getName(), buffer.toString())
                )
        );
        return Block.block(
                """
                        ${buffer}.append("${leftBracket}");
                        ${writeStructCode}
                        ${buffer}.append("${rightBracket}");
                        """,
                Map.ofEntries(
                        Map.entry("buffer", buffer),
                        Map.entry("leftBracket", Cast.leftBracket),
                        Map.entry("rightBracket", Cast.rightBracket),
                        Map.entry("writeStructCode", writeStructCode)
                )
        );
    }

    private CastFunction castToBinaryCode(DataType from) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            String utf8StringClass = UTF8String.class.getName();
            String numberConverterClass = NumberConverter.class.getName();
            if (from instanceof StringType) {
                return Block.block(
                        "${value} = ((${utf8String}) ${input}).getBytes();",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("utf8String", utf8StringClass),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (from instanceof IntegerType || from instanceof LongType) {
                return Block.block(
                        "${value} = ${numberConverter}.toBinary(${input});",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("numberConverter", numberConverterClass),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to binary");
        };
    }

    private CastFunction castToBooleanCode(DataType from, CodegenContext ctx) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            String utf8StringClass = UTF8String.class.getName();
            String stringUtilsClass = StringUtils.class.getName();
            if (from instanceof StringType) {
                return Block.block(
                        """
                                if (${stringUtils}.isTrueString((${utf8String}) ${input})) {
                                  ${isNull} = false;
                                  ${value} = true;
                                } else if (${stringUtils}.isFalseString((${utf8String}) ${input})) {
                                  ${isNull} = false;
                                  ${value} = false;
                                } else {
                                  ${isNull} = true;
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("stringUtils", stringUtilsClass),
                                Map.entry("utf8String", utf8StringClass),
                                Map.entry("input", inputExpr),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof BooleanType) {
                return Block.block(
                        "${value} = ${input};",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (from instanceof DecimalType) {
                String decimalClass = Decimal.class.getName();
                return Block.block(
                        "${value} = ((${decimalClass}) ${input}) != ${decimalClass}.ZERO;",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("decimalClass", decimalClass),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (isSimpleNumeric(from)) {
                return Block.block(
                        "${value} = ${input} != 0;",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to boolean");
        };
    }

    private CastFunction castToDateCode(DataType from, CodegenContext ctx) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            String dateTimeUtils = JippleDateTimeUtils.class.getName();
            if (from instanceof StringType) {
                String dateOpt = ctx.freshName("dateOpt");
                return Block.block(
                        """
                                com.jipple.collection.Option<Integer> ${dateOpt} =
                                  ${dateTimeUtils}.stringToDate((UTF8String) ${input});
                                if (${dateOpt}.isDefined()) {
                                  ${isNull} = false;
                                  ${value} = ${dateOpt}.get();
                                } else {
                                  ${isNull} = true;
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("dateOpt", dateOpt),
                                Map.entry("dateTimeUtils", dateTimeUtils),
                                Map.entry("input", inputExpr),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof TimestampType) {
                ZoneId zid = zoneId();
                String zoneRef = ctx.addReferenceObj("zoneId", zid, ZoneId.class.getName());
                return Block.block(
                        "${value} = ${dateTimeUtils}.microsToDays((Long) ${input}, ${zoneId});",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("dateTimeUtils", dateTimeUtils),
                                Map.entry("input", inputExpr),
                                Map.entry("zoneId", zoneRef)
                        )
                );
            }
            if (from instanceof TimestampNTZType) {
                return Block.block(
                        "${value} = ${dateTimeUtils}.microsToDays((Long) ${input}, java.time.ZoneOffset.UTC);",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("dateTimeUtils", dateTimeUtils),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to date");
        };
    }

    private CastFunction castToTimestampCode(DataType from, CodegenContext ctx) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            String dateTimeUtils = JippleDateTimeUtils.class.getName();
            if (from instanceof StringType) {
                String tsOpt = ctx.freshName("tsOpt");
                ZoneId zid = zoneId();
                String zoneRef = ctx.addReferenceObj("zoneId", zid, ZoneId.class.getName());
                return Block.block(
                        """
                                com.jipple.collection.Option<Long> ${tsOpt} =
                                  ${dateTimeUtils}.stringToTimestamp((UTF8String) ${input}, ${zoneId});
                                if (${tsOpt}.isDefined()) {
                                  ${isNull} = false;
                                  ${value} = ${tsOpt}.get();
                                } else {
                                  ${isNull} = true;
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("tsOpt", tsOpt),
                                Map.entry("dateTimeUtils", dateTimeUtils),
                                Map.entry("input", inputExpr),
                                Map.entry("zoneId", zoneRef),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof DateType) {
                ZoneId zid = zoneId();
                String zoneRef = ctx.addReferenceObj("zoneId", zid, ZoneId.class.getName());
                return Block.block(
                        "${value} = ${dateTimeUtils}.daysToMicros((Integer) ${input}, ${zoneId});",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("dateTimeUtils", dateTimeUtils),
                                Map.entry("input", inputExpr),
                                Map.entry("zoneId", zoneRef)
                        )
                );
            }
            if (from instanceof TimestampNTZType) {
                ZoneId zid = zoneId();
                String zoneRef = ctx.addReferenceObj("zoneId", zid, ZoneId.class.getName());
                return Block.block(
                        "${value} = ${dateTimeUtils}.convertTz((Long) ${input}, ${zoneId}, java.time.ZoneOffset.UTC);",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("dateTimeUtils", dateTimeUtils),
                                Map.entry("input", inputExpr),
                                Map.entry("zoneId", zoneRef)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to timestamp");
        };
    }

    private CastFunction castToTimestampNTZCode(DataType from, CodegenContext ctx) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            String dateTimeUtils = JippleDateTimeUtils.class.getName();
            if (from instanceof StringType) {
                String tsOpt = ctx.freshName("tsNtzOpt");
                return Block.block(
                        """
                                com.jipple.collection.Option<Long> ${tsOpt} =
                                  ${dateTimeUtils}.stringToTimestamp((UTF8String) ${input}, java.time.ZoneOffset.UTC);
                                if (${tsOpt}.isDefined()) {
                                  ${isNull} = false;
                                  ${value} = ${tsOpt}.get();
                                } else {
                                  ${isNull} = true;
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("tsOpt", tsOpt),
                                Map.entry("dateTimeUtils", dateTimeUtils),
                                Map.entry("input", inputExpr),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof DateType) {
                return Block.block(
                        "${value} = ${dateTimeUtils}.daysToMicros((Integer) ${input}, java.time.ZoneOffset.UTC);",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("dateTimeUtils", dateTimeUtils),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (from instanceof TimestampType) {
                ZoneId zid = zoneId();
                String zoneRef = ctx.addReferenceObj("zoneId", zid, ZoneId.class.getName());
                return Block.block(
                        "${value} = ${dateTimeUtils}.convertTz((Long) ${input}, java.time.ZoneOffset.UTC, ${zoneId});",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("dateTimeUtils", dateTimeUtils),
                                Map.entry("input", inputExpr),
                                Map.entry("zoneId", zoneRef)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to timestamp_ntz");
        };
    }

    private CastFunction castToIntCode(DataType from, CodegenContext ctx) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            if (from instanceof StringType) {
                String intWrapper = ctx.freshName("intWrapper");
                return Block.block(
                        """
                                UTF8String.IntWrapper ${intWrapper} = new UTF8String.IntWrapper();
                                if (!((UTF8String) ${input}).toInt(${intWrapper})) {
                                  ${isNull} = true;
                                } else {
                                  ${isNull} = false;
                                  ${value} = ${intWrapper}.value;
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("intWrapper", intWrapper),
                                Map.entry("input", inputExpr),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof BooleanType) {
                return Block.block(
                        "${value} = ${input} ? 1 : 0;",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (from instanceof DateType) {
                return Block.block(
                        "${isNull} = true;",
                        Map.ofEntries(
                                Map.entry("isNull", evNull)
                        )
                );
            }
            if (from instanceof TimestampType) {
                return Block.block(
                        "${value} = (int) java.lang.Math.floorDiv((Long) ${input}, ${microsPerSecond});",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr),
                                Map.entry("microsPerSecond", "com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND")
                        )
                );
            }
            if (from instanceof DecimalType) {
                return Block.block(
                        "${value} = ((${decimalClass}) ${input}).toInt();",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("decimalClass", Decimal.class.getName()),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (isSimpleNumeric(from)) {
                return Block.block(
                        "${value} = (int) ${input};",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to int");
        };
    }

    private CastFunction castToLongCode(DataType from, CodegenContext ctx) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            if (from instanceof StringType) {
                String longWrapper = ctx.freshName("longWrapper");
                return Block.block(
                        """
                                UTF8String.LongWrapper ${longWrapper} = new UTF8String.LongWrapper();
                                if (!((UTF8String) ${input}).toLong(${longWrapper})) {
                                  ${isNull} = true;
                                } else {
                                  ${isNull} = false;
                                  ${value} = ${longWrapper}.value;
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("longWrapper", longWrapper),
                                Map.entry("input", inputExpr),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof BooleanType) {
                return Block.block(
                        "${value} = ${input} ? 1L : 0L;",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (from instanceof DateType) {
                return Block.block(
                        "${isNull} = true;",
                        Map.ofEntries(
                                Map.entry("isNull", evNull)
                        )
                );
            }
            if (from instanceof TimestampType) {
                return Block.block(
                        "${value} = java.lang.Math.floorDiv((Long) ${input}, ${microsPerSecond});",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr),
                                Map.entry("microsPerSecond", "com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND")
                        )
                );
            }
            if (from instanceof DecimalType) {
                return Block.block(
                        "${value} = ((${decimalClass}) ${input}).toLong();",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("decimalClass", Decimal.class.getName()),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (isSimpleNumeric(from)) {
                return Block.block(
                        "${value} = (long) ${input};",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to long");
        };
    }

    private CastFunction castToFloatCode(DataType from, CodegenContext ctx) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            String castClass = Cast.class.getName();
            if (from instanceof StringType) {
                String floatStr = ctx.freshName("floatStr");
                return Block.block(
                        """
                                String ${floatStr} = ((UTF8String) ${input}).toString();
                                try {
                                  ${value} = Float.parseFloat(${floatStr});
                                } catch (NumberFormatException e) {
                                  Object special = ${castClass}.processFloatingPointSpecialLiterals(${floatStr}, true);
                                  if (special != null) {
                                    ${value} = (Float) special;
                                  } else {
                                    ${isNull} = true;
                                  }
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("floatStr", floatStr),
                                Map.entry("input", inputExpr),
                                Map.entry("value", evPrim),
                                Map.entry("castClass", castClass),
                                Map.entry("isNull", evNull)
                        )
                );
            }
            if (from instanceof BooleanType) {
                return Block.block(
                        "${value} = ${input} ? 1.0f : 0.0f;",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (from instanceof DateType) {
                return Block.block(
                        "${isNull} = true;",
                        Map.ofEntries(
                                Map.entry("isNull", evNull)
                        )
                );
            }
            if (from instanceof TimestampType) {
                return Block.block(
                        "${value} = (float) (((double) ${input}) / ${microsPerSecond});",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr),
                                Map.entry("microsPerSecond", "com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND")
                        )
                );
            }
            if (from instanceof DecimalType) {
                return Block.block(
                        "${value} = ((${decimalClass}) ${input}).toFloat();",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("decimalClass", Decimal.class.getName()),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (isSimpleNumeric(from)) {
                return Block.block(
                        "${value} = (float) ${input};",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to float");
        };
    }

    private CastFunction castToDoubleCode(DataType from, CodegenContext ctx) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            String castClass = Cast.class.getName();
            if (from instanceof StringType) {
                String doubleStr = ctx.freshName("doubleStr");
                return Block.block(
                        """
                                String ${doubleStr} = ((UTF8String) ${input}).toString();
                                try {
                                  ${value} = Double.parseDouble(${doubleStr});
                                } catch (NumberFormatException e) {
                                  Object special = ${castClass}.processFloatingPointSpecialLiterals(${doubleStr}, false);
                                  if (special != null) {
                                    ${value} = (Double) special;
                                  } else {
                                    ${isNull} = true;
                                  }
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("doubleStr", doubleStr),
                                Map.entry("input", inputExpr),
                                Map.entry("value", evPrim),
                                Map.entry("castClass", castClass),
                                Map.entry("isNull", evNull)
                        )
                );
            }
            if (from instanceof BooleanType) {
                return Block.block(
                        "${value} = ${input} ? 1.0 : 0.0;",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (from instanceof DateType) {
                return Block.block(
                        "${isNull} = true;",
                        Map.ofEntries(
                                Map.entry("isNull", evNull)
                        )
                );
            }
            if (from instanceof TimestampType) {
                return Block.block(
                        "${value} = ((double) ${input}) / ${microsPerSecond};",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr),
                                Map.entry("microsPerSecond", "com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND")
                        )
                );
            }
            if (from instanceof DecimalType) {
                return Block.block(
                        "${value} = ((${decimalClass}) ${input}).toDouble();",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("decimalClass", Decimal.class.getName()),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            if (isSimpleNumeric(from)) {
                return Block.block(
                        "${value} = (double) ${input};",
                        Map.ofEntries(
                                Map.entry("value", evPrim),
                                Map.entry("input", inputExpr)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to double");
        };
    }

    private CastFunction castToDecimalCode(DataType from, DecimalType target, CodegenContext ctx) {
        return (c, evPrim, evNull) -> {
            String inputExpr = c.toString();
            String decimalClass = Decimal.class.getName();
            String precision = String.valueOf(target.precision);
            String scale = String.valueOf(target.scale);
            if (from instanceof StringType) {
                String tmp = ctx.freshName("tmpDecimal");
                String casted = ctx.freshName("castedDecimal");
                return Block.block(
                        """
                                ${decimalClass} ${tmp} = ${decimalClass}.fromString((UTF8String) ${input});
                                if (${tmp} == null) {
                                  ${isNull} = true;
                                } else {
                                  ${decimalClass} ${casted} =
                                    ${tmp}.toPrecision(${precision}, ${scale}, java.math.RoundingMode.HALF_UP, true);
                                  if (${casted} == null) {
                                    ${isNull} = true;
                                  } else {
                                    ${isNull} = false;
                                    ${value} = ${casted};
                                  }
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("decimalClass", decimalClass),
                                Map.entry("tmp", tmp),
                                Map.entry("casted", casted),
                                Map.entry("input", inputExpr),
                                Map.entry("precision", precision),
                                Map.entry("scale", scale),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof BooleanType) {
                String casted = ctx.freshName("castedDecimal");
                return Block.block(
                        """
                                ${decimalClass} ${casted} =
                                  (${input} ? ${decimalClass}.ONE : ${decimalClass}.ZERO)
                                    .toPrecision(${precision}, ${scale}, java.math.RoundingMode.HALF_UP, true);
                                if (${casted} == null) {
                                  ${isNull} = true;
                                } else {
                                  ${isNull} = false;
                                  ${value} = ${casted};
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("decimalClass", decimalClass),
                                Map.entry("casted", casted),
                                Map.entry("input", inputExpr),
                                Map.entry("precision", precision),
                                Map.entry("scale", scale),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof DecimalType) {
                String casted = ctx.freshName("castedDecimal");
                return Block.block(
                        """
                                ${decimalClass} ${casted} =
                                  ((${decimalClass}) ${input})
                                    .toPrecision(${precision}, ${scale}, java.math.RoundingMode.HALF_UP, true);
                                if (${casted} == null) {
                                  ${isNull} = true;
                                } else {
                                  ${isNull} = false;
                                  ${value} = ${casted};
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("decimalClass", decimalClass),
                                Map.entry("casted", casted),
                                Map.entry("input", inputExpr),
                                Map.entry("precision", precision),
                                Map.entry("scale", scale),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof IntegerType || from instanceof LongType) {
                String tmp = ctx.freshName("tmpDecimal");
                String casted = ctx.freshName("castedDecimal");
                return Block.block(
                        """
                                ${decimalClass} ${tmp} = new ${decimalClass}(${input});
                                ${decimalClass} ${casted} =
                                  ${tmp}.toPrecision(${precision}, ${scale}, java.math.RoundingMode.HALF_UP, true);
                                if (${casted} == null) {
                                  ${isNull} = true;
                                } else {
                                  ${isNull} = false;
                                  ${value} = ${casted};
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("decimalClass", decimalClass),
                                Map.entry("tmp", tmp),
                                Map.entry("casted", casted),
                                Map.entry("input", inputExpr),
                                Map.entry("precision", precision),
                                Map.entry("scale", scale),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof FloatType) {
                String tmp = ctx.freshName("tmpDecimal");
                String casted = ctx.freshName("castedDecimal");
                return Block.block(
                        """
                                if (Float.isNaN(${input}) || Float.isInfinite(${input})) {
                                  ${isNull} = true;
                                } else {
                                  ${decimalClass} ${tmp} = new ${decimalClass}((float) ${input});
                                  ${decimalClass} ${casted} =
                                    ${tmp}.toPrecision(${precision}, ${scale}, java.math.RoundingMode.HALF_UP, true);
                                  if (${casted} == null) {
                                    ${isNull} = true;
                                  } else {
                                    ${isNull} = false;
                                    ${value} = ${casted};
                                  }
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("decimalClass", decimalClass),
                                Map.entry("tmp", tmp),
                                Map.entry("casted", casted),
                                Map.entry("input", inputExpr),
                                Map.entry("precision", precision),
                                Map.entry("scale", scale),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            if (from instanceof DoubleType) {
                String tmp = ctx.freshName("tmpDecimal");
                String casted = ctx.freshName("castedDecimal");
                return Block.block(
                        """
                                if (Double.isNaN(${input}) || Double.isInfinite(${input})) {
                                  ${isNull} = true;
                                } else {
                                  ${decimalClass} ${tmp} = new ${decimalClass}((double) ${input});
                                  ${decimalClass} ${casted} =
                                    ${tmp}.toPrecision(${precision}, ${scale}, java.math.RoundingMode.HALF_UP, true);
                                  if (${casted} == null) {
                                    ${isNull} = true;
                                  } else {
                                    ${isNull} = false;
                                    ${value} = ${casted};
                                  }
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("decimalClass", decimalClass),
                                Map.entry("tmp", tmp),
                                Map.entry("casted", casted),
                                Map.entry("input", inputExpr),
                                Map.entry("precision", precision),
                                Map.entry("scale", scale),
                                Map.entry("isNull", evNull),
                                Map.entry("value", evPrim)
                        )
                );
            }
            throw new UnsupportedOperationException("Cannot cast " + from + " to decimal");
        };
    }

    private CastFunction castArrayCode(
            DataType fromType,
            DataType toType,
            CodegenContext ctx) {
        CastFunction elementCast = nullSafeCastFunction(fromType, toType, ctx);
        String arrayClass = GenericArrayData.class.getName();
        ExprValue fromElementNull = ctx.freshVariable("feNull", BooleanType.INSTANCE);
        ExprValue fromElementPrim = ctx.freshVariable("fePrim", fromType);
        ExprValue toElementNull = ctx.freshVariable("teNull", BooleanType.INSTANCE);
        ExprValue toElementPrim = ctx.freshVariable("tePrim", toType);
        ExprValue size = ctx.freshVariable("n", IntegerType.INSTANCE);
        ExprValue j = ctx.freshVariable("j", IntegerType.INSTANCE);
        ExprValue values = ctx.freshVariable("values", Object[].class);
        String javaType = CodeGeneratorUtils.javaType(fromType);

        return (c, evPrim, evNull) -> {
            String fromElementValue = CodeGeneratorUtils.getValue(c.toString(), fromType, j.toString());
            Block castBlock = castCode(ctx, fromElementPrim, fromElementNull, toElementPrim, toElementNull, toType, elementCast);
            return Block.block(
                    """
                            final int ${size} = ${input}.numElements();
                            final Object[] ${values} = new Object[${size}];
                            for (int ${j} = 0; ${j} < ${size}; ${j}++) {
                              if (${input}.isNullAt(${j})) {
                                ${values}[${j}] = null;
                              } else {
                                boolean ${fromElementNull} = false;
                                ${javaType} ${fromElementPrim} = ${fromElementValue};
                                ${castBlock}
                                if (${toElementNull}) {
                                  ${values}[${j}] = null;
                                } else {
                                  ${values}[${j}] = ${toElementPrim};
                                }
                              }
                            }
                            ${evPrim} = new ${arrayClass}(${values});
                            """,
                    Map.ofEntries(
                            Map.entry("size", size),
                            Map.entry("input", c),
                            Map.entry("values", values),
                            Map.entry("j", j),
                            Map.entry("fromElementNull", fromElementNull),
                            Map.entry("javaType", javaType),
                            Map.entry("fromElementPrim", fromElementPrim),
                            Map.entry("fromElementValue", fromElementValue),
                            Map.entry("castBlock", castBlock),
                            Map.entry("toElementNull", toElementNull),
                            Map.entry("toElementPrim", toElementPrim),
                            Map.entry("evPrim", evPrim),
                            Map.entry("arrayClass", arrayClass)
                    )
            );
        };
    }

    private CastFunction castMapCode(MapType from, MapType to, CodegenContext ctx) {
        CastFunction keysCast = castArrayCode(from.keyType, to.keyType, ctx);
        CastFunction valuesCast = castArrayCode(from.valueType, to.valueType, ctx);

        String mapClass = ArrayBasedMapData.class.getName();
        ExprValue keys = ctx.freshVariable("keys", new ArrayType(from.keyType));
        ExprValue convertedKeys = ctx.freshVariable("convertedKeys", new ArrayType(to.keyType));
        ExprValue convertedKeysNull = ctx.freshVariable("convertedKeysNull", BooleanType.INSTANCE);
        ExprValue values = ctx.freshVariable("values", new ArrayType(from.valueType));
        ExprValue convertedValues = ctx.freshVariable("convertedValues", new ArrayType(to.valueType));
        ExprValue convertedValuesNull = ctx.freshVariable("convertedValuesNull", BooleanType.INSTANCE);

        Block keysCastBlock = castCode(
                ctx,
                keys,
                FalseLiteral.INSTANCE,
                convertedKeys,
                convertedKeysNull,
                new ArrayType(to.keyType),
                keysCast);
        Block valuesCastBlock = castCode(
                ctx,
                values,
                FalseLiteral.INSTANCE,
                convertedValues,
                convertedValuesNull,
                new ArrayType(to.valueType),
                valuesCast);

        return (c, evPrim, evNull) -> Block.block(
                """
                        final ArrayData ${keys} = ${input}.keyArray();
                        final ArrayData ${values} = ${input}.valueArray();
                        ${keysCastBlock}
                        ${valuesCastBlock}
                        ${evPrim} = new ${mapClass}(${convertedKeys}, ${convertedValues});
                        """,
                Map.ofEntries(
                        Map.entry("keys", keys),
                        Map.entry("values", values),
                        Map.entry("input", c),
                        Map.entry("keysCastBlock", keysCastBlock),
                        Map.entry("valuesCastBlock", valuesCastBlock),
                        Map.entry("evPrim", evPrim),
                        Map.entry("mapClass", mapClass),
                        Map.entry("convertedKeys", convertedKeys),
                        Map.entry("convertedValues", convertedValues)
                )
        );
    }

    private CastFunction castStructCode(
            StructType from,
            StructType to,
            CodegenContext ctx) {
        int fieldCount = from.fields.length;
        CastFunction[] fieldCasts = new CastFunction[fieldCount];
        for (int idx = 0; idx < fieldCount; idx++) {
            fieldCasts[idx] = nullSafeCastFunction(from.fields[idx].dataType, to.fields[idx].dataType, ctx);
        }

        ExprValue tmpResult = ctx.freshVariable("tmpResult", GenericInternalRow.class);
        ExprValue tmpInput = ctx.freshVariable("tmpInput", InternalRow.class);
        String rowClass = GenericInternalRow.class.getName();

        List<Block> fieldBlocks = new ArrayList<>();
        for (int i = 0; i < fieldCount; i++) {
            DataType fromType = from.fields[i].dataType;
            DataType toType = to.fields[i].dataType;
            ExprValue fromFieldPrim = ctx.freshVariable("ffp", fromType);
            ExprValue fromFieldNull = ctx.freshVariable("ffn", BooleanType.INSTANCE);
            ExprValue toFieldPrim = ctx.freshVariable("tfp", toType);
            ExprValue toFieldNull = ctx.freshVariable("tfn", BooleanType.INSTANCE);
            String javaType = CodeGeneratorUtils.javaType(fromType);
            String getValue = CodeGeneratorUtils.getValue(tmpInput.toString(), fromType, String.valueOf(i));
            String setColumn = CodeGeneratorUtils.setColumn(tmpResult.toString(), toType, i, toFieldPrim.toString()) + ";";
            Block castBlock = castCode(ctx, fromFieldPrim, fromFieldNull, toFieldPrim, toFieldNull, toType, fieldCasts[i]);

            fieldBlocks.add(Block.block(
                    """
                            boolean ${fromFieldNull} = ${tmpInput}.isNullAt(${index});
                            if (${fromFieldNull}) {
                              ${tmpResult}.setNullAt(${index});
                            } else {
                              ${javaType} ${fromFieldPrim} = ${getValue};
                              ${castBlock}
                              if (${toFieldNull}) {
                                ${tmpResult}.setNullAt(${index});
                              } else {
                                ${setColumn}
                              }
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("fromFieldNull", fromFieldNull),
                            Map.entry("tmpInput", tmpInput),
                            Map.entry("index", i),
                            Map.entry("tmpResult", tmpResult),
                            Map.entry("javaType", javaType),
                            Map.entry("fromFieldPrim", fromFieldPrim),
                            Map.entry("getValue", getValue),
                            Map.entry("castBlock", castBlock),
                            Map.entry("toFieldNull", toFieldNull),
                            Map.entry("setColumn", setColumn)
                    )
            ));
        }

        List<String> fieldsEvalCode = new ArrayList<>(fieldBlocks.size());
        for (Block block : fieldBlocks) {
            fieldsEvalCode.add(block.toString());
        }
        String fieldsEvalCodes = ctx.splitExpressions(
                fieldsEvalCode,
                "castStruct",
                List.of(
                        com.jipple.tuple.Tuple2.of("InternalRow", tmpInput.toString()),
                        com.jipple.tuple.Tuple2.of(rowClass, tmpResult.toString())
                )
        );

        return (c, evPrim, evNull) -> Block.block(
                """
                        final ${rowClass} ${tmpResult} = new ${rowClass}(${fieldCount});
                        final InternalRow ${tmpInput} = (InternalRow) ${input};
                        ${fieldsEvalCode}
                        ${evPrim} = ${tmpResult};
                        """,
                Map.ofEntries(
                        Map.entry("rowClass", rowClass),
                        Map.entry("tmpResult", tmpResult),
                        Map.entry("fieldCount", fieldCount),
                        Map.entry("tmpInput", tmpInput),
                        Map.entry("input", c),
                        Map.entry("fieldsEvalCode", fieldsEvalCodes),
                        Map.entry("evPrim", evPrim)
                )
        );
    }

    private static boolean isSimpleNumeric(DataType dataType) {
        return dataType instanceof IntegerType
                || dataType instanceof LongType
                || dataType instanceof FloatType
                || dataType instanceof DoubleType;
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
