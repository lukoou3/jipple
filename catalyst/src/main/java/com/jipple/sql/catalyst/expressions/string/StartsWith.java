package com.jipple.sql.catalyst.expressions.string;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.unsafe.types.UTF8String;

import java.util.Map;

public class StartsWith extends StringPredicate {
    public StartsWith(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public boolean compare(UTF8String l, UTF8String r) {
        return l.startsWith(r);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return defineCodeGen(ctx, ev, (c1, c2) ->
                CodeGeneratorUtils.template(
                        "(${c1}).startsWith(${c2})",
                        Map.of(
                                "c1", c1,
                                "c2", c2
                        )
                ));
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new StartsWith(newLeft, newRight);
    }

}
