package com.jipple.sql.catalyst.expressions.predicate;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.BinaryOperator;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.arithmetic.Add;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.FalseLiteral;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class And extends BinaryOperator {
    public And(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public AbstractDataType inputType() {
        return BOOLEAN;
    }

    @Override
    public String symbol() {
        return "&&";
    }

    @Override
    public String sqlOperator() {
        return "AND";
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    // +---------+---------+---------+---------+
    // | AND     | TRUE    | FALSE   | UNKNOWN |
    // +---------+---------+---------+---------+
    // | TRUE    | TRUE    | FALSE   | UNKNOWN |
    // | FALSE   | FALSE   | FALSE   | FALSE   |
    // | UNKNOWN | UNKNOWN | FALSE   | UNKNOWN |
    // +---------+---------+---------+---------+

    @Override
    public Object eval(InternalRow input) {
        Object input1 = left.eval(input);
        if (Boolean.FALSE.equals(input1)) {
            return false;
        } else {
            Object input2 = right.eval(input);
            if (Boolean.FALSE.equals(input2)) {
                return false;
            } else {
                if (input1 != null && input2 != null) {
                    return true;
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

        // The result should be `false`, if any of them is `false` whenever the other is null or not.
        if (!left.nullable() && !right.nullable()) {
            return ev.copy(Block.block(
                    """
                            ${leftCode}
                            boolean ${value} = false;

                            if (${leftValue}) {
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
                            boolean ${value} = false;

                            if (!${leftIsNull} && !${leftValue}) {
                            } else {
                              ${rightCode}
                              if (!${rightIsNull} && !${rightValue}) {
                              } else if (!${leftIsNull} && !${rightIsNull}) {
                                ${value} = true;
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
        return new And(newLeft, newRight);
    }
}
