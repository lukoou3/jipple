package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.sql.types.*;
import com.jipple.unsafe.types.UTF8String;

import java.util.function.Function;

public class Cast extends UnaryExpression implements TimeZoneAwareExpression<Cast> {
    public final DataType dataType;
    public final Option<String> timeZoneId;
    private transient Function<Object, Object> _cast;

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

    private Function<Object, Object> castToString(DataType from) {
        if (from instanceof StringType) {
            return x -> x;
        } else {
            return x -> UTF8String.fromString(x.toString());
        }
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
            } else {
                throw new UnsupportedOperationException("Cannot cast " + from + " to " + to);
            }
        }
    }

    public Function<Object, Object> cast() {
        if (_cast == null) {
            _cast = castInternal(child.dataType(), dataType);
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
    private static boolean forceNullable(DataType from, DataType to) {
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
