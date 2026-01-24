package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.*;

import java.util.List;
import java.util.function.Function;

import static com.jipple.sql.types.DataTypes.NUMERIC;

public class UnaryMinus extends UnaryExpression {
    Function<Object, Object> negate;
    public UnaryMinus(Expression child) {
        super(child);
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(NUMERIC));
    }

    @Override
    public DataType dataType() {
        return child.dataType();
    }

    @Override
    protected Object nullSafeEval(Object input) {
        if (negate == null) {
            DataType dataType = dataType();
            if (dataType instanceof IntegerType) {
                negate = (x) -> -((Integer) x);
            } else if (dataType instanceof LongType) {
                negate = (x) -> -((Long) x);
            } else if (dataType instanceof DoubleType) {
                negate = (x) -> -((Double) x);
            } else {
                throw new RuntimeException();
            }
        }
        return negate.apply(input);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        DataType dt = dataType();
        if (dt instanceof DecimalType) {
            return defineCodeGen(ctx, ev, c -> c + ".unaryMinus()");
        } else if (dt instanceof NumericType) {
            return nullSafeCodeGen(ctx, ev, eval -> {
                String originValue = ctx.freshName("origin");
                String javaType = CodeGeneratorUtils.javaType(dt);
                return CodeGeneratorUtils.template(
                        """
                                ${javaType} ${originValue} = (${javaType})(${eval});
                                ${value} = (${javaType})(-(${originValue}));
                                """,
                        java.util.Map.of(
                                "javaType", javaType,
                                "originValue", originValue,
                                "eval", eval,
                                "value", ev.value
                        )
                );
            });
        }
        throw new UnsupportedOperationException("Unsupported data type: " + dt);
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new UnaryMinus(newChild);
    }
}
