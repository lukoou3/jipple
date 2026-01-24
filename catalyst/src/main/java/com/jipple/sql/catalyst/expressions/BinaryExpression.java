package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.FalseLiteral;
import com.jipple.sql.catalyst.trees.BinaryLike;
import com.jipple.sql.errors.QueryExecutionErrors;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class BinaryExpression extends Expression implements BinaryLike<Expression> {
    public final Expression left;
    public final Expression right;

    public BinaryExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Object[] args() {
        return new Object[] { left, right };
    }

    @Override
    public final List<Expression> children() {
        return List.of(left, right);
    }

    @Override
    public Expression left() {
        return left;
    }

    @Override
    public Expression right() {
        return right;
    }

    @Override
    public boolean foldable() {
        return left.foldable() && right.foldable();
    }

    @Override
    public boolean nullable() {
        return left.nullable() || right.nullable();
    }

    /**
     * Default behavior of evaluation according to the default nullability of BinaryExpression.
     * If subclass of BinaryExpression override nullable, probably should also override this.
     */
    @Override
    public Object eval(InternalRow input) {
        var value1 = left.eval(input);
        if (value1 == null) {
            return null;
        } else {
            var value2 = right.eval(input);
            if (value2 == null) {
                return null;
            } else {
                return nullSafeEval(value1, value2);
            }
        }
    }

    /**
     * Called by default [[eval]] implementation.  If subclass of BinaryExpression keep the default
     * nullability, they can override this method to save null-check code.  If we need full control
     * of evaluation process, we should override [[eval]].
     */
    protected Object nullSafeEval(Object input1, Object input2) {
        throw QueryExecutionErrors.notOverrideExpectedMethodsError("BinaryExpressions",
                "eval", "nullSafeEval");
    }

    /**
     * Short hand for generating binary evaluation code.
     * If either of the sub-expressions is null, the result of this computation
     * is assumed to be null.
     *
     * @param f accepts two variable names and returns Java code to compute the output.
     */
    protected ExprCode defineCodeGen(
            CodegenContext ctx,
            ExprCode ev,
            BiFunction<String, String, String> f) {
        return nullSafeCodeGen(ctx, ev, (eval1, eval2) ->
                ev.value + " = " + f.apply(eval1, eval2) + ";");
    }

    /**
     * Short hand for generating binary evaluation code.
     * If either of the sub-expressions is null, the result of this computation
     * is assumed to be null.
     *
     * @param f function that accepts the 2 non-null evaluation result names of children
     *          and returns Java code to compute the output.
     */
    protected ExprCode nullSafeCodeGen(
            CodegenContext ctx,
            ExprCode ev,
            BiFunction<String, String, String> f) {
        ExprCode leftGen = left.genCode(ctx);
        ExprCode rightGen = right.genCode(ctx);
        String resultCode = f.apply(leftGen.value.toString(), rightGen.value.toString());

        if (nullable()) {
            String nullSafeEval = leftGen.code + ctx.nullSafeExec(
                    left.nullable(),
                    leftGen.isNull.toString(),
                    rightGen.code + ctx.nullSafeExec(
                            right.nullable(),
                            rightGen.isNull.toString(),
                            CodeGeneratorUtils.template("\n" + """
                                            ${isNull} = false; // resultCode could change nullability.
                                            ${resultCode}
                                            """,
                                    Map.of(
                                            "isNull", ev.isNull.toString(),
                                            "resultCode", resultCode
                                    )
                            )
                    )
            );

            return ev.copy(Block.block(
                    """
                            boolean ${isNull} = true;
                            ${javaType} ${value} = ${defaultValue};
                            ${nullSafeEval}
                            """,
                    Map.of(
                            "isNull", ev.isNull,
                            "javaType", CodeGeneratorUtils.javaType(dataType()),
                            "value", ev.value,
                            "defaultValue", CodeGeneratorUtils.defaultValue(dataType()),
                            "nullSafeEval", nullSafeEval
                    )
            ));
        } else {
            return ev.copy(Block.block(
                            """
                                    ${leftCode}
                                    ${rightCode}
                                    ${javaType} ${value} = ${defaultValue};
                                    ${resultCode}
                                    """,
                            Map.of(
                                    "leftCode", leftGen.code,
                                    "rightCode", rightGen.code,
                                    "javaType", CodeGeneratorUtils.javaType(dataType()),
                                    "value", ev.value,
                                    "defaultValue", CodeGeneratorUtils.defaultValue(dataType()),
                                    "resultCode", resultCode
                            )
                    ),
                    FalseLiteral.INSTANCE);
        }
    }

    @Override
    public Expression mapChildren(Function<Expression, Expression> f) {
        return BinaryLike.mapChildren(this, f);
    }

    @Override
    protected final Expression withNewChildrenInternal(List<Expression> newChildren) {
        return BinaryLike.withNewChildrenInternal(this, newChildren);
    }

}
