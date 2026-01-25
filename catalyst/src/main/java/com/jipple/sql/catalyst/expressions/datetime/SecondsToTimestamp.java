package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.Decimal;
import com.jipple.sql.types.DecimalType;
import com.jipple.sql.types.DoubleType;
import com.jipple.sql.types.FloatType;
import com.jipple.sql.types.IntegralType;
import com.jipple.sql.types.NumericType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND;
import static com.jipple.sql.types.DataTypes.NUMERIC;
import static com.jipple.sql.types.DataTypes.TIMESTAMP;

public class SecondsToTimestamp extends UnaryExpression {
    private transient Function<Object, Object> _evalFunc;

    public SecondsToTimestamp(Expression child) {
        super(child);
    }

    @Override
    public DataType dataType() {
        return TIMESTAMP;
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(NUMERIC));
    }

    @Override
    public boolean nullable() {
        DataType childType = child.dataType();
        if (childType instanceof FloatType || childType instanceof DoubleType) {
            return true;
        }
        return child.nullable();
    }

    @Override
    protected Object nullSafeEval(Object input) {
        return evalFunc().apply(input);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        DataType childType = child.dataType();
        if (childType instanceof IntegralType) {
            return defineCodeGen(ctx, ev,
                    c -> CodeGeneratorUtils.template(
                            "java.lang.Math.multiplyExact(${value}, 1000000L)",
                            Map.of("value", c)
                    ));
        }
        if (childType instanceof DecimalType) {
            String operand = "new java.math.BigDecimal(1000000)";
            return defineCodeGen(ctx, ev,
                    c -> c + ".toBigDecimal().multiply(" + operand + ").longValueExact()");
        }
        if (childType instanceof NumericType) {
            String castToDouble = childType instanceof FloatType ? "(double)" : "";
            String boxedType = CodeGeneratorUtils.boxedType(childType);
            return nullSafeCodeGen(ctx, ev, c -> CodeGeneratorUtils.template(
                    """
                            if (${boxedType}.isNaN(${value}) || ${boxedType}.isInfinite(${value})) {
                              ${isNull} = true;
                            } else {
                              ${result} = (long)(${castToDouble}${value} * 1000000L);
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("boxedType", boxedType),
                            Map.entry("value", c),
                            Map.entry("isNull", ev.isNull),
                            Map.entry("result", ev.value),
                            Map.entry("castToDouble", castToDouble)
                    )
            ));
        }
        throw new UnsupportedOperationException(childType.sql());
    }

    @Override
    public String prettyName() {
        return "timestamp_seconds";
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new SecondsToTimestamp(newChild);
    }

    private Function<Object, Object> evalFunc() {
        if (_evalFunc == null) {
            DataType childType = child.dataType();
            if (childType instanceof IntegralType) {
                _evalFunc = input -> Math.multiplyExact(((Number) input).longValue(), MICROS_PER_SECOND);
            } else if (childType instanceof DecimalType) {
                BigDecimal operand = new BigDecimal(MICROS_PER_SECOND);
                _evalFunc = input -> ((Decimal) input).toBigDecimal().multiply(operand).longValueExact();
            } else if (childType instanceof FloatType) {
                _evalFunc = input -> {
                    Float f = (Float) input;
                    if (f.isNaN() || f.isInfinite()) {
                        return null;
                    }
                    return (long) (f.doubleValue() * MICROS_PER_SECOND);
                };
            } else if (childType instanceof DoubleType) {
                _evalFunc = input -> {
                    Double d = (Double) input;
                    if (d.isNaN() || d.isInfinite()) {
                        return null;
                    }
                    return (long) (d * MICROS_PER_SECOND);
                };
            } else {
                _evalFunc = input -> {
                    throw new UnsupportedOperationException(childType.sql());
                };
            }
        }
        return _evalFunc;
    }
}
