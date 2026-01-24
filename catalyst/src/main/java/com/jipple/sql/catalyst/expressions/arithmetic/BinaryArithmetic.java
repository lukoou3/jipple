package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.sql.catalyst.expressions.BinaryOperator;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.DoubleType;
import com.jipple.sql.types.FloatType;
import com.jipple.sql.types.IntegerType;
import com.jipple.sql.types.LongType;

import java.util.Map;
import java.util.function.BiFunction;

public abstract class BinaryArithmetic extends BinaryOperator {
    public BinaryArithmetic(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public DataType dataType() {
        return left.dataType();
    }

    protected boolean failOnError() {
        return false;
    }


    /** Name of the function for this expression on a [[Decimal]] type. */
    protected String decimalMethod() {
        throw QueryExecutionErrors.notOverrideExpectedMethodsError("BinaryArithmetics",
                "decimalMethod", "genCode");
    }


    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        DataType dt = dataType();
        if (dt instanceof IntegerType
                || dt instanceof LongType
                || dt instanceof DoubleType
                || dt instanceof FloatType) {
            // When Double/Float overflows, there can be 2 cases:
            // - precision loss: according to SQL standard, the number is truncated;
            // - returns (+/-)Infinite: same behavior also other DBs have (e.g. Postgres)
            return nullSafeCodeGen(ctx, ev, (eval1, eval2) -> CodeGeneratorUtils.template(
                    "${value} = ${eval1} ${symbol} ${eval2};",
                    Map.of(
                            "value", ev.value.toString(),
                            "eval1", eval1,
                            "symbol", symbol(),
                            "eval2", eval2
                    )
            ));
        }
        throw new UnsupportedOperationException("Unsupported data type: " + dt);
    }

    @Override
    protected ExprCode nullSafeCodeGen(
            CodegenContext ctx,
            ExprCode ev,
            BiFunction<String, String, String> f) {
        boolean evalModeTry = false; // evalMode == EvalMode.TRY
        if (evalModeTry) {
            BiFunction<String, String, String> tryBlock = (eval1, eval2) -> CodeGeneratorUtils.template(
                    """
                            try {
                              ${body}
                            } catch (Exception e) {
                              ${isNull} = true;
                            }
                            """,
                    Map.of(
                            "body", f.apply(eval1, eval2),
                            "isNull", ev.isNull.toString()
                    )
            );
            return super.nullSafeCodeGen(ctx, ev, tryBlock);
        } else {
            return super.nullSafeCodeGen(ctx, ev, f);
        }
    }
}
