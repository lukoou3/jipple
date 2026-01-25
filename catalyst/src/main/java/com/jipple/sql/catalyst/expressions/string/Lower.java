package com.jipple.sql.catalyst.expressions.string;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.unsafe.types.UTF8String;

public class Lower extends String2StringExpression {
    public Lower(Expression child) {
        super(child);
    }

    @Override
    protected UTF8String convert(UTF8String value) {
        return value.toLowerCase();
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return defineCodeGen(ctx, ev, c -> "(" + c + ").toLowerCase()");
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new Lower(newChild);
    }
}
