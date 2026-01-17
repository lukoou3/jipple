package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;
import com.jipple.sql.catalyst.types.DataTypeUtils;
import com.jipple.sql.types.*;

import java.util.List;

import static com.jipple.sql.types.DataTypes.*;

public final class TypeCoercion {
    private TypeCoercion() {}

    // See https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Types.
    // The conversion for integral and floating point types have a linear widening hierarchy:
    private static final List<NumericType> numericPrecedence = List.of(INTEGER, LONG,FLOAT,DOUBLE);

    public static List<Rule<LogicalPlan>> typeCoercionRules() {
        return List.of(
            new ImplicitTypeCasts()
        );
    }


    public static Option<DataType> findTightestCommonType(DataType t1, DataType t2) {
        if (t1.equals(t2)) {
            return Option.some(t1);
        }
        if (t1 instanceof NullType) {
            return Option.some(t2);
        }
        if (t2 instanceof NullType) {
            return Option.some(t1);
        }

        if (t1 instanceof IntegralType && t2 instanceof DecimalType decimal && decimal.isWiderThan(t1)) {
            return Option.some(t2);
        }
        if (t1 instanceof DecimalType decimal && t2 instanceof IntegralType && decimal.isWiderThan(t2)) {
            return Option.some(t1);
        }

        if (t1 instanceof NumericType && t2 instanceof NumericType
            && !(t1 instanceof DecimalType) && !(t2 instanceof DecimalType)) {
            int index = -1;
            for (int i = numericPrecedence.size() - 1; i >= 0; i--) {
                NumericType type = numericPrecedence.get(i);
                if (type.equals(t1) || type.equals(t2)) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                return Option.some(numericPrecedence.get(index));
            }
        }

        if (t1 instanceof DatetimeType d1 && t2 instanceof DatetimeType d2) {
            return Option.some(findWiderDateTimeType(d1, d2));
        }

        return Option.none();
    }

    public static DatetimeType findWiderDateTimeType(DatetimeType d1, DatetimeType d2) {
        if ((d1 instanceof TimestampType && d2 instanceof DateType)
            || (d1 instanceof DateType && d2 instanceof TimestampType)) {
            return TimestampType.INSTANCE;
        }
        if ((d1 instanceof TimestampType && d2 instanceof TimestampNTZType)
            || (d1 instanceof TimestampNTZType && d2 instanceof TimestampType)) {
            return TimestampType.INSTANCE;
        }
        if ((d1 instanceof TimestampNTZType && d2 instanceof DateType)
            || (d1 instanceof DateType && d2 instanceof TimestampNTZType)) {
            return TimestampNTZType.INSTANCE;
        }
        throw new IllegalArgumentException(
            "Unsupported datetime types: " + d1.getClass().getSimpleName() + ", " + d2.getClass().getSimpleName());
    }

    /**
     * Check whether the given types are equal ignoring nullable, containsNull and valueContainsNull.
     */
    public static boolean haveSameType(List<DataType> types) {
        if (types.size() <= 1) {
            return true;
        } else {
            DataType head = types.get(0);
            return types.stream().allMatch(e -> DataTypeUtils.sameType(e, head));
        }
    }


    public static Option<Expression> implicitCast(Expression expression, AbstractDataType expectedType) {
        return implicitCast(expression.dataType(), expectedType).map(dataType -> {
            if (dataType.equals(expression.dataType())) {
                return expression;
            }
            return new Cast(expression, dataType);
        });
    }

    private static Option<DataType> implicitCast(DataType inType, AbstractDataType expectedType) {
        DataType ret = null;
        if (expectedType.acceptsType(inType)) {
            ret = inType;
        } else if (inType instanceof NullType) {
            ret = expectedType.defaultConcreteType();
        } else if (inType instanceof StringType && expectedType.equals(NUMERIC)) {
            ret = NUMERIC.defaultConcreteType();
        } else if (inType instanceof NumericType && expectedType instanceof DecimalType) {
            ret = DecimalType.forType(inType);
        } else if (inType instanceof NumericType && expectedType instanceof NumericType) {
            ret = (DataType) expectedType;
        } else if (inType instanceof NumericType && expectedType.equals(NUMERIC)) {
            ret = NUMERIC.defaultConcreteType();
        } else if (inType instanceof DatetimeType && expectedType instanceof DatetimeType) {
            ret = (DataType) expectedType;
        } else if (inType instanceof DatetimeType && expectedType instanceof AnyTimestampType) {
            ret = expectedType.defaultConcreteType();
        } else if (inType instanceof StringType && expectedType instanceof DecimalType) {
            ret = DecimalType.SYSTEM_DEFAULT;
        } else if (inType instanceof StringType && expectedType instanceof NumericType) {
            ret = (DataType) expectedType;
        } else if (inType instanceof StringType && expectedType instanceof DatetimeType) {
            ret = (DataType) expectedType;
        } else if (inType instanceof StringType && expectedType instanceof AnyTimestampType) {
            ret = expectedType.defaultConcreteType();
        } else if (inType instanceof StringType && expectedType instanceof BinaryType) {
            ret = BinaryType.INSTANCE;
        } else if (inType instanceof AtomicType && expectedType instanceof StringType && !(inType instanceof StringType)) {
            ret = StringType.INSTANCE;
        } else if (expectedType instanceof TypeCollection typeCollection) {
            for (AbstractDataType candidate : typeCollection.types) {
                Option<DataType> candidateType = implicitCast(inType, candidate);
                if (candidateType.isDefined()) {
                    ret = candidateType.get();
                    break;
                }
            }
        } else if (inType instanceof ArrayType fromArray && expectedType instanceof ArrayType toArray) {
            if (toArray.containsNull) {
                Option<DataType> elementType = implicitCast(fromArray.elementType, toArray.elementType);
                if (elementType.isDefined()) {
                    ret = new ArrayType(elementType.get(), true);
                }
            } else if (fromArray.containsNull) {
                ret = null;
            } else if (!Cast.forceNullable(fromArray.elementType, toArray.elementType)) {
                Option<DataType> elementType = implicitCast(fromArray.elementType, toArray.elementType);
                if (elementType.isDefined()) {
                    ret = new ArrayType(elementType.get(), false);
                }
            }
        }
        return Option.option(ret);
    }

}
