package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.codegen.*;
import com.jipple.sql.catalyst.trees.QuaternaryLike;
import com.jipple.sql.errors.QueryExecutionErrors;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * An expression with four inputs and one output. The output is by default evaluated to null
 * if any input is evaluated to null.
 */
public abstract class QuaternaryExpression extends Expression implements QuaternaryLike<Expression> {
    public final Expression first;
    public final Expression second;
    public final Expression third;
    public final Expression fourth;

    protected QuaternaryExpression(Expression first, Expression second, Expression third, Expression fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    @Override
    public Object[] args() {
        return new Object[] { first, second, third, fourth };
    }

    @Override
    public final List<Expression> children() {
        return List.of(first, second, third, fourth);
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
    public Expression fourth() {
        return fourth;
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
     * Default behavior of evaluation according to the default nullability of QuaternaryExpression.
     * If subclass of QuaternaryExpression override nullable, probably should also override this.
     */
    @Override
    public Object eval(InternalRow input) {
        var value1 = first.eval(input);
        if (value1 != null) {
            var value2 = second.eval(input);
            if (value2 != null) {
                var value3 = third.eval(input);
                if (value3 != null) {
                    var value4 = fourth.eval(input);
                    if (value4 != null) {
                        return nullSafeEval(value1, value2, value3, value4);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Called by default [[eval]] implementation.  If subclass of QuaternaryExpression keep the
     *  default nullability, they can override this method to save null-check code.  If we need
     *  full control of evaluation process, we should override [[eval]].
     */
    protected Object nullSafeEval(Object input1, Object input2, Object input3, Object input4) {
        throw QueryExecutionErrors.notOverrideExpectedMethodsError("QuaternaryExpression",
                "eval", "nullSafeEval");
    }

    /**
     * Short hand for generating quaternary evaluation code.
     * If either of the sub-expressions is null, the result of this computation
     * is assumed to be null.
     *
     * @param f accepts four variable names and returns Java code to compute the output.
     */
    protected ExprCode defineCodeGen(
            CodegenContext ctx,
            ExprCode ev,
            QuaternaryFunction<String, String, String, String, String> f) {
        return nullSafeCodeGen(ctx, ev, (eval1, eval2, eval3, eval4) ->
                ev.value + " = " + f.apply(eval1, eval2, eval3, eval4) + ";");
    }

    /**
     * Short hand for generating quaternary evaluation code.
     * If either of the sub-expressions is null, the result of this computation
     * is assumed to be null.
     *
     * @param f function that accepts the 4 non-null evaluation result names of children
     *          and returns Java code to compute the output.
     */
    protected ExprCode nullSafeCodeGen(
            CodegenContext ctx,
            ExprCode ev,
            QuaternaryFunction<String, String, String, String, String> f) {
        ExprCode firstGen = children().get(0).genCode(ctx);
        ExprCode secondGen = children().get(1).genCode(ctx);
        ExprCode thirdGen = children().get(2).genCode(ctx);
        ExprCode fourthGen = children().get(3).genCode(ctx);
        String resultCode = f.apply(firstGen.value.toString(), secondGen.value.toString(),
                thirdGen.value.toString(), fourthGen.value.toString());

        if (nullable()) {
            String nullSafeEval = firstGen.code + ctx.nullSafeExec(children().get(0).nullable(), firstGen.isNull.toString(),
                    secondGen.code + ctx.nullSafeExec(children().get(1).nullable(), secondGen.isNull.toString(),
                            thirdGen.code + ctx.nullSafeExec(children().get(2).nullable(), thirdGen.isNull.toString(),
                                    fourthGen.code + ctx.nullSafeExec(children().get(3).nullable(), fourthGen.isNull.toString(),
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
                                    ${firstCode}
                                    ${secondCode}
                                    ${thirdCode}
                                    ${fourthCode}
                                    ${javaType} ${value} = ${defaultValue};
                                    ${resultCode}
                                    """,
                            Map.of(
                                    "firstCode", firstGen.code,
                                    "secondCode", secondGen.code,
                                    "thirdCode", thirdGen.code,
                                    "fourthCode", fourthGen.code,
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
        return QuaternaryLike.mapChildren(this, f);
    }

    @Override
    protected final Expression withNewChildrenInternal(List<Expression> newChildren) {
        return QuaternaryLike.withNewChildrenInternal(this, newChildren);
    }

    @FunctionalInterface
    protected interface QuaternaryFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }
}
