package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.collection.Option;
import com.jipple.sql.SQLConf;
import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Resolver;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.catalyst.rules.Rule;
import com.jipple.sql.catalyst.types.DataTypeUtils;
import com.jipple.sql.types.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiFunction;

import static com.jipple.sql.types.DataTypes.*;

public final class TypeCoercion {
    private TypeCoercion() {}

    // See https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Types.
    // The conversion for integral and floating point types have a linear widening hierarchy:
    private static final List<NumericType> numericPrecedence = List.of(INTEGER, LONG,FLOAT,DOUBLE);

    public static List<Rule<LogicalPlan>> typeCoercionRules() {
        Rule<LogicalPlan>[] rules = new Rule[]{
                new ImplicitTypeCasts(),
                new IfCoercion(),
                new InConversion(),
                new PromoteStrings(),
                new ConcatCoercion(),
                new CaseWhenCoercion(),
                new IntegralDivision(),
                new BooleanEquality(),
        };
        return List.of(rules);
    }

    public static boolean canCast(DataType from, DataType to) {
        return Cast.canCast(from, to);
    }

    public static Option<DataType> findTypeForComplex(
            DataType t1,
            DataType t2,
            BiFunction<DataType, DataType, Option<DataType>> findTypeFunc) {
        if (t1 instanceof ArrayType a1 && t2 instanceof ArrayType a2) {
            return findTypeFunc.apply(a1.elementType, a2.elementType).map(et ->
                    new ArrayType(
                            et,
                            a1.containsNull || a2.containsNull
                                    || Cast.forceNullable(a1.elementType, et)
                                    || Cast.forceNullable(a2.elementType, et)));
        }
        if (t1 instanceof MapType m1 && t2 instanceof MapType m2) {
            return findTypeFunc.apply(m1.keyType, m2.keyType)
                    .filter(kt -> !Cast.forceNullable(m1.keyType, kt) && !Cast.forceNullable(m2.keyType, kt))
                    .flatMap(kt -> findTypeFunc.apply(m1.valueType, m2.valueType)
                    .map(vt -> new MapType(kt, vt,m1.valueContainsNull || m2.valueContainsNull
                                            || Cast.forceNullable(m1.valueType, vt)
                                            || Cast.forceNullable(m2.valueType, vt))
                    ));
        }
        if (t1 instanceof StructType s1
                && t2 instanceof StructType s2
                && s1.fields.length == s2.fields.length) {
            Resolver resolver = Resolver.caseSensitiveResolution();
            ArrayList<StructField> fields = new ArrayList<>(s1.fields.length);
            for (int i = 0; i < s1.fields.length; i++) {
                StructField field1 = s1.fields[i];
                StructField field2 = s2.fields[i];
                if (!resolver.resolve(field1.name, field2.name)) {
                    return Option.none();
                }
                Option<DataType> dataType = findTypeFunc.apply(field1.dataType, field2.dataType);
                if (dataType.isEmpty()) {
                    return Option.none();
                }
                DataType dt = dataType.get();
                boolean nullable = field1.nullable
                        || field2.nullable
                        || Cast.forceNullable(field1.dataType, dt)
                        || Cast.forceNullable(field2.dataType, dt);
                fields.add(new StructField(field1.name, dt, nullable));
            }
            return Option.some(new StructType(fields.toArray(new StructField[0])));
        }
        return Option.none();
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

    public static Expression castIfNotSameType(Expression expr, DataType dt) {
        if (!expr.dataType().sameType(dt)) {
            return new Cast(expr, dt);
        } else {
            return expr;
        }
    }

    public static Expression castIfNotEquals(Expression expr, DataType dt) {
        if (!expr.dataType().equals(dt)) {
            return new Cast(expr, dt);
        } else {
            return expr;
        }
    }

    // Return whether a string literal can be promoted as the given data type in a binary comparison.
    private static boolean canPromoteAsInBinaryComparison(DataType dataType) {
        // There is no need to add `Cast` for comparison between strings.
        if (dataType instanceof StringType) {
            return false;
        }
        return dataType instanceof AtomicType;
    }

    /**
     * This function determines the target type of a comparison operator when one operand
     * is a String and the other is not. It also handles when one op is a Date and the
     * other is a Timestamp by making the target type to be String.
     */
    public static Option<DataType> findCommonTypeForBinaryComparison(DataType dt1,  DataType dt2,  SQLConf conf) {
        if (dt1 instanceof StringType && dt2 instanceof DateType) {
            return Option.some(conf.castDatetimeToString() ? STRING : DATE);
        }
        if (dt1 instanceof DateType && dt2 instanceof StringType) {
            return Option.some(conf.castDatetimeToString() ? STRING : DATE);
        }
        if (dt1 instanceof StringType && dt2 instanceof TimestampType) {
            return Option.some(conf.castDatetimeToString() ? STRING : TIMESTAMP);
        }
        if (dt1 instanceof TimestampType && dt2 instanceof StringType) {
            return Option.some(conf.castDatetimeToString() ? STRING : TIMESTAMP);
        }
        if (dt1 instanceof StringType && dt2 instanceof NullType) {
            return Option.some(STRING);
        }
        if (dt1 instanceof NullType && dt2 instanceof StringType) {
            return Option.some(STRING);
        }

        // Cast to TimestampType when we compare DateType with TimestampType
        if (dt1 instanceof TimestampType && dt2 instanceof DateType) {
            return Option.some(TIMESTAMP);
        }
        if (dt1 instanceof DateType && dt2 instanceof TimestampType) {
            return Option.some(TIMESTAMP);
        }

        // There is no proper decimal type we can pick, using double type is the best we can do.
        if (dt1 instanceof DecimalType && dt2 instanceof StringType) {
            return Option.some(DOUBLE);
        }
        if (dt1 instanceof StringType && dt2 instanceof DecimalType) {
            return Option.some(DOUBLE);
        }

        if (dt1 instanceof StringType && dt2 instanceof AtomicType && canPromoteAsInBinaryComparison(dt2)) {
            return Option.some(dt2);
        }
        if (dt1 instanceof AtomicType && dt2 instanceof StringType && canPromoteAsInBinaryComparison(dt1)) {
            return Option.some(dt1);
        }
        return Option.none();
    }

    public static Option<DataType> findWiderTypeForTwo(DataType t1, DataType t2) {
        return findTightestCommonType(t1, t2);
    }

    public static Option<DataType> findWiderCommonType(List<DataType> types) {
        LinkedHashSet<DataType> stringTypes = new LinkedHashSet<>();
        LinkedHashSet<DataType> nonStringTypes = new LinkedHashSet<>();
        for (DataType type : types) {
            if (hasStringType(type)) {
                stringTypes.add(type);
            } else {
                nonStringTypes.add(type);
            }
        }
        LinkedHashSet<DataType> orderedTypes = new LinkedHashSet<>();
        orderedTypes.addAll(stringTypes);
        orderedTypes.addAll(nonStringTypes);
        Option<DataType> result = Option.some(NULL);
        for (DataType type : orderedTypes) {
            if (result.isEmpty()) {
                return Option.none();
            }
            result = findWiderTypeForTwo(result.get(), type);
        }
        return result;
    }

    /**
     * Whether the data type contains StringType.
     */
    public static boolean hasStringType(DataType dataType) {
        if (dataType instanceof StringType) {
            return true;
        }
        if (dataType instanceof ArrayType arrayType) {
            return hasStringType(arrayType.elementType);
        }
        return false;
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

    /**
     * The method finds a common type for data types that differ only in nullable flags, including
     * `nullable`, `containsNull` of [[ArrayType]] and `valueContainsNull` of [[MapType]].
     * If the input types are different besides nullable flags, None is returned.
     */
    public static Option<DataType> findCommonTypeDifferentOnlyInNullFlags(DataType t1, DataType t2) {
        if (t1.equals(t2)) {
            return Option.some(t1);
        } else {
            return findTypeForComplex(t1, t2, TypeCoercion::findCommonTypeDifferentOnlyInNullFlags);
        }
    }

    public static Option<DataType> findCommonTypeDifferentOnlyInNullFlags(List<DataType> types) {
        if (types.isEmpty()) {
            return Option.none();
        }
        Option<DataType> acc = Option.some(types.get(0));
        for (int i = 1; i < types.size(); i++) {
            if (acc.isDefined()) {
                acc = findCommonTypeDifferentOnlyInNullFlags(acc.get(), types.get(i));
            } else {
                return Option.none();
            }
        }
        return acc;
    }


}
