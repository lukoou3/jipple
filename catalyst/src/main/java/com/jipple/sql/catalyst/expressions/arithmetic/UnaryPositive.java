package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;

import java.util.List;

import static com.jipple.sql.types.DataTypes.NUMERIC;

public class UnaryPositive extends UnaryExpression {
    public UnaryPositive(Expression child) {
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
        return input;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return defineCodeGen(ctx, ev, c -> c);
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new UnaryPositive(newChild);
    }
}
