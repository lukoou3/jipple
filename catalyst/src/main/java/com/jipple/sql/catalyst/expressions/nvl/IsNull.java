package com.jipple.sql.catalyst.expressions.nvl;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.FalseLiteral;
import com.jipple.sql.types.DataType;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class IsNull extends UnaryExpression {

    public IsNull(Expression child) {
        super(child);
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    @Override
    public Object eval(InternalRow input) {
        return child.eval(input) == null;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        ExprCode eval = child.genCode(ctx);
        return new ExprCode(eval.code, FalseLiteral.INSTANCE, eval.isNull);
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new IsNull(newChild);
    }
}
