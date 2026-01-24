package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;

import java.util.List;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class Not extends UnaryExpression {
    public Not(Expression child) {
        super(child);
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(BOOLEAN));
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    @Override
    protected Object nullSafeEval(Object input) {
        return !(Boolean)input;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return defineCodeGen(ctx, ev, c -> "!(" + c + ")");
    }

    @Override
    public String toString() {
        return "NOT " + child.toString();
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new Not(newChild);
    }
}
