package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;

import java.util.List;
import java.util.Map;

import static com.jipple.sql.types.DataTypes.INTEGRAL;
import static com.jipple.sql.types.DataTypes.TIMESTAMP;

public abstract class IntegralToTimestampBase extends UnaryExpression {

    public IntegralToTimestampBase(Expression child) {
        super(child);
    }

    protected abstract long upScaleFactor();

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(INTEGRAL));
    }

    @Override
    public DataType dataType() {
        return TIMESTAMP;
    }

    @Override
    protected Object nullSafeEval(Object input) {
        return Math.multiplyExact(((Number) input).longValue(), upScaleFactor());
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        if (upScaleFactor() == 1L) {
            return defineCodeGen(ctx, ev, c -> c);
        }
        return defineCodeGen(ctx, ev, c -> CodeGeneratorUtils.template(
                "java.lang.Math.multiplyExact(${value}, ${factor}L)",
                Map.of(
                        "value", c,
                        "factor", upScaleFactor()
                )
        ));
    }
}
