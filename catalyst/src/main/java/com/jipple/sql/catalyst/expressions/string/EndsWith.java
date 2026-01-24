package com.jipple.sql.catalyst.expressions.string;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.unsafe.types.UTF8String;

import java.util.Map;

public class EndsWith extends StringPredicate {
    public EndsWith(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public boolean compare(UTF8String l, UTF8String r) {
        return l.endsWith(r);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return defineCodeGen(ctx, ev, (c1, c2) ->
                CodeGeneratorUtils.template(
                        "(${c1}).endsWith(${c2})",
                        Map.of(
                                "c1", c1,
                                "c2", c2
                        )
                ));
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new EndsWith(newLeft, newRight);
    }

}
