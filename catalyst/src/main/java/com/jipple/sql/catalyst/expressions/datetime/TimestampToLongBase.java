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

import static com.jipple.sql.types.DataTypes.LONG;
import static com.jipple.sql.types.DataTypes.TIMESTAMP;

public abstract class TimestampToLongBase extends UnaryExpression {
    public TimestampToLongBase(Expression child) {
        super(child);
    }

    protected abstract long scaleFactor();

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(TIMESTAMP));
    }

    @Override
    public DataType dataType() {
        return LONG;
    }

    @Override
    protected Object nullSafeEval(Object input) {
        return Math.floorDiv(((Number) input).longValue(), scaleFactor());
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        if (scaleFactor() == 1L) {
            return defineCodeGen(ctx, ev, c -> c);
        }
        return defineCodeGen(ctx, ev, c -> CodeGeneratorUtils.template(
                "java.lang.Math.floorDiv(${value}, ${factor}L)",
                Map.of(
                        "value", c,
                        "factor", scaleFactor()
                )
        ));
    }
}
