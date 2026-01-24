package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.BinaryOperator;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.FalseLiteral;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class Or extends BinaryOperator {
    public Or(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public AbstractDataType inputType() {
        return BOOLEAN;
    }

    @Override
    public String symbol() {
        return "||";
    }

    @Override
    public String sqlOperator() {
        return "OR";
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    // +---------+---------+---------+---------+
    // | OR      | TRUE    | FALSE   | UNKNOWN |
    // +---------+---------+---------+---------+
    // | TRUE    | TRUE    | TRUE    | TRUE    |
    // | FALSE   | TRUE    | FALSE   | UNKNOWN |
    // | UNKNOWN | TRUE    | UNKNOWN | UNKNOWN |
    // +---------+---------+---------+---------+
    @Override
    public Object eval(InternalRow input) {
        Object input1 = left.eval(input);
        if (Boolean.TRUE.equals(input1)) {
            return true;
        } else {
            Object input2 = right.eval(input);
            if (Boolean.TRUE.equals(input2)) {
                return true;
            } else {
                if (input1 != null && input2 != null) {
                    return false;
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        ExprCode eval1 = left.genCode(ctx);
        ExprCode eval2 = right.genCode(ctx);

        // The result should be `true`, if any of them is `true` whenever the other is null or not.
        if (!left.nullable() && !right.nullable()) {
            return ev.copy(Block.block(
                    """
                            ${leftCode}
                            boolean ${value} = true;

                            if (!${leftValue}) {
                              ${rightCode}
                              ${value} = ${rightValue};
                            }
                            """,
                    java.util.Map.of(
                            "leftCode", eval1.code,
                            "value", ev.value,
                            "leftValue", eval1.value,
                            "rightCode", eval2.code,
                            "rightValue", eval2.value
                    )
            ), FalseLiteral.INSTANCE);
        } else {
            return ev.copy(Block.block(
                    """
                            ${leftCode}
                            boolean ${isNull} = false;
                            boolean ${value} = true;

                            if (!${leftIsNull} && ${leftValue}) {
                            } else {
                              ${rightCode}
                              if (!${rightIsNull} && ${rightValue}) {
                              } else if (!${leftIsNull} && !${rightIsNull}) {
                                ${value} = false;
                              } else {
                                ${isNull} = true;
                              }
                            }
                            """,
                    java.util.Map.of(
                            "leftCode", eval1.code,
                            "isNull", ev.isNull,
                            "value", ev.value,
                            "leftIsNull", eval1.isNull,
                            "leftValue", eval1.value,
                            "rightCode", eval2.code,
                            "rightIsNull", eval2.isNull,
                            "rightValue", eval2.value
                    )
            ));
        }
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Or(newLeft, newRight);
    }
}
