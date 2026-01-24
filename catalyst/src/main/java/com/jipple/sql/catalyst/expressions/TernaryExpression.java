package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.FalseLiteral;
import com.jipple.sql.catalyst.trees.TernaryLike;
import com.jipple.sql.errors.QueryExecutionErrors;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class TernaryExpression extends Expression implements TernaryLike<Expression> {
    public final Expression first;
    public final Expression second;
    public final Expression third;

    public TernaryExpression(Expression first, Expression second, Expression third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public Object[] args() {
        return new Object[] { first, second, third };
    }

    @Override
    public final List<Expression> children() {
        return List.of(first, second, third);
    }

    @Override
    public Expression first() {
        return first;
    }

    @Override
    public Expression second() {
        return second;
    }

    @Override
    public Expression third() {
        return third;
    }

    @Override
    public boolean foldable() {
        return children().stream().allMatch(x -> x.foldable());
    }

    @Override
    public boolean nullable() {
        return children().stream().anyMatch(x -> x.nullable());
    }

    /**
     * Default behavior of evaluation according to the default nullability of TernaryExpression.
     * If subclass of TernaryExpression override nullable, probably should also override this.
     */
    @Override
    public Object eval(InternalRow input) {
        var value1 = first.eval(input);
        if (value1 != null) {
            var value2 = second.eval(input);
            if (value2 != null) {
                var value3 = third.eval(input);
                if (value3 != null) {
                    return nullSafeEval(value1, value2, value3);
                }
            }
        }
        return null;
    }

    /**
     * Called by default [[eval]] implementation.  If subclass of TernaryExpression keep the default
     * nullability, they can override this method to save null-check code.  If we need full control
     * of evaluation process, we should override [[eval]].
     */
    protected Object nullSafeEval(Object input1, Object input2, Object input3) {
        throw QueryExecutionErrors.notOverrideExpectedMethodsError("TernaryExpressions",
                "eval", "nullSafeEval");
    }

    /**
     * Short hand for generating ternary evaluation code.
     * If either of the sub-expressions is null, the result of this computation
     * is assumed to be null.
     *
     * @param f accepts three variable names and returns Java code to compute the output.
     */
    protected ExprCode defineCodeGen(
            CodegenContext ctx,
            ExprCode ev,
            TriFunction<String, String, String, String> f) {
        return nullSafeCodeGen(ctx, ev, (eval1, eval2, eval3) ->
                ev.value + " = " + f.apply(eval1, eval2, eval3) + ";");
    }

    /**
     * Short hand for generating ternary evaluation code.
     * If either of the sub-expressions is null, the result of this computation
     * is assumed to be null.
     *
     * @param f function that accepts the 3 non-null evaluation result names of children
     *          and returns Java code to compute the output.
     */
    protected ExprCode nullSafeCodeGen(
            CodegenContext ctx,
            ExprCode ev,
            TriFunction<String, String, String, String> f) {
        ExprCode leftGen = children().get(0).genCode(ctx);
        ExprCode midGen = children().get(1).genCode(ctx);
        ExprCode rightGen = children().get(2).genCode(ctx);
        String resultCode = f.apply(leftGen.value.toString(), midGen.value.toString(), rightGen.value.toString());

        if (nullable()) {
            String nullSafeEval = leftGen.code + ctx.nullSafeExec(
                    children().get(0).nullable(),
                    leftGen.isNull.toString(),
                    midGen.code + ctx.nullSafeExec(
                            children().get(1).nullable(),
                            midGen.isNull.toString(),
                            rightGen.code + ctx.nullSafeExec(
                                    children().get(2).nullable(),
                                    rightGen.isNull.toString(),
                                    CodeGeneratorUtils.template(
                                            """
                                                    ${isNull} = false; // resultCode could change nullability.
                                                    ${resultCode}
                                                    """,
                                            Map.of(
                                                    "isNull", ev.isNull,
                                                    "resultCode", resultCode
                                            )
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
                                    ${midCode}
                                    ${rightCode}
                                    ${javaType} ${value} = ${defaultValue};
                                    ${resultCode}
                                    """,
                            Map.of(
                                    "leftCode", leftGen.code,
                                    "midCode", midGen.code,
                                    "rightCode", rightGen.code,
                                    "javaType", CodeGeneratorUtils.javaType(dataType()),
                                    "value", ev.value,
                                    "defaultValue", CodeGeneratorUtils.defaultValue(dataType()),
                                    "resultCode", resultCode
                            )
                    ),
                    FalseLiteral.INSTANCE,
                    ev.value);
        }
    }

    @Override
    public Expression mapChildren(Function<Expression, Expression> f) {
        return TernaryLike.mapChildren(this, f);
    }

    @Override
    protected final Expression withNewChildrenInternal(List<Expression> newChildren) {
        return TernaryLike.withNewChildrenInternal(this, newChildren);
    }

    @FunctionalInterface
    protected interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

}
