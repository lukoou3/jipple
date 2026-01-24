package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.FalseLiteral;

import java.util.Map;

public class EqualNullSafe extends BinaryComparison {
    public EqualNullSafe(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String symbol() {
        return "<=>";
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public Object eval(InternalRow input) {
        Object input1 = left.eval(input);
        Object input2 = right.eval(input);
        if (input1 == null && input2 == null) {
            return true;
        } else if (input1 == null || input2 == null) {
            return false;
        } else {
            return comparator().compare(input1, input2) == 0;
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        ExprCode eval1 = left.genCode(ctx);
        ExprCode eval2 = right.genCode(ctx);
        String equalCode = ctx.genEqual(left.dataType(), eval1.value.toString(), eval2.value.toString());
        return ev.copy(
                eval1.code
                        .plus(eval2.code)
                        .plus(Block.block(
                                """
                                        boolean ${value} = (${leftIsNull} && ${rightIsNull}) ||
                                           (!${leftIsNull} && !${rightIsNull} && ${equalCode});
                                        """,
                                Map.of(
                                        "value", ev.value,
                                        "leftIsNull", eval1.isNull,
                                        "rightIsNull", eval2.isNull,
                                        "equalCode", equalCode
                                )
                        )),
                FalseLiteral.INSTANCE,
                ev.value);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new EqualNullSafe(newLeft, newRight);
    }
}
