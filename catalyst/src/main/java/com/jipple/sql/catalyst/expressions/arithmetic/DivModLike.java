package com.jipple.sql.catalyst.expressions.arithmetic;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.DecimalType;

import java.util.Map;

// Common base trait for Divide and Remainder, since these two classes are almost identical
public abstract class DivModLike extends BinaryArithmetic  {
    public DivModLike(Expression left, Expression right) {
        super(left, right);
    }

    protected String decimalToDataTypeCodeGen(String decimalResult) {
        return decimalResult;
    }

    // Whether we should check overflow or not in ANSI mode.
    protected boolean checkDivideOverflow() {
        return false;
    }

    @Override
    public boolean nullable() {
        return true;
    }

    @Override
    public final Object eval(InternalRow input) {
        // evaluate right first as we have a chance to skip left if right is 0
        Object input2 = right.eval(input);
        if (input2 == null || ((Number)input2).intValue() == 0) {
            return null;
        }  else {
            Object input1 = left.eval(input);
            if (input1 == null) {
                return null;
            } else {
                return evalOperation(input1, input2);
            }
        }
    }

    /**
     * Special case handling due to division/remainder by 0 => null or ArithmeticException.
     */
    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        ExprCode eval1 = left.genCode(ctx);
        ExprCode eval2 = right.genCode(ctx);
        DataType operandsDataType = left.dataType();
        String isZero = operandsDataType instanceof DecimalType
                ? eval2.value + ".isZero()"
                : eval2.value + " == 0";
        String javaType = CodeGeneratorUtils.javaType(dataType());
        String errorContextCode = "null"; // getContextOrNullCode(ctx, failOnError)

        DataType superDataType = super.dataType();
        String operation;
        if (superDataType instanceof DecimalType decimalType) {
            String decimalValue = ctx.freshName("decimalValue");
            operation = CodeGeneratorUtils.template(
                    """
                            Decimal ${decimalValue} = ${eval1}.${decimalMethod}(${eval2}).toPrecision(
                              ${precision}, ${scale}, Decimal.ROUND_HALF_UP, ${nullOnOverflow}, ${errorContextCode});
                            if (${decimalValue} != null) {
                              ${value} = ${decimalToDataType};
                            } else {
                              ${isNull} = true;
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("decimalValue", decimalValue),
                            Map.entry("eval1", eval1.value.toString()),
                            Map.entry("decimalMethod", decimalMethod()),
                            Map.entry("eval2", eval2.value.toString()),
                            Map.entry("precision", decimalType.precision),
                            Map.entry("scale", decimalType.scale),
                            Map.entry("nullOnOverflow", !failOnError()),
                            Map.entry("errorContextCode", errorContextCode),
                            Map.entry("value", ev.value.toString()),
                            Map.entry("decimalToDataType", decimalToDataTypeCodeGen(decimalValue)),
                            Map.entry("isNull", ev.isNull.toString())
                    )
            );
        } else {
            operation = CodeGeneratorUtils.template(
                    "${value} = (${javaType})(${eval1} ${symbol} ${eval2});",
                    Map.of(
                            "value", ev.value.toString(),
                            "javaType", javaType,
                            "eval1", eval1.value.toString(),
                            "symbol", symbol(),
                            "eval2", eval2.value.toString()
                    )
            );
        }

        String checkIntegralDivideOverflow = "";
        if (checkDivideOverflow()) {
            checkIntegralDivideOverflow = CodeGeneratorUtils.template(
                    """
                            if (${eval1} == ${minValue}L && ${eval2} == -1)
                              throw QueryExecutionErrors.overflowInIntegralDivideError(${errorContextCode});
                            """,
                    Map.of(
                            "eval1", eval1.value.toString(),
                            "minValue", Long.MIN_VALUE,
                            "eval2", eval2.value.toString(),
                            "errorContextCode", errorContextCode
                    )
            );
        }

        if (!left.nullable() && !right.nullable()) {
            String divByZero = failOnError()
                    ? "throw QueryExecutionErrors.divideByZeroError(" + errorContextCode + ");"
                    : ev.isNull + " = true;";
            return ev.copy(Block.block(
                    """
                            ${rightCode}
                            boolean ${isNull} = false;
                            ${javaType} ${value} = ${defaultValue};
                            if (${isZero}) {
                              ${divByZero}
                            } else {
                              ${leftCode}
                              ${checkOverflow}
                              ${operation}
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("rightCode", eval2.code.toString()),
                            Map.entry("isNull", ev.isNull.toString()),
                            Map.entry("javaType", javaType),
                            Map.entry("value", ev.value.toString()),
                            Map.entry("defaultValue", CodeGeneratorUtils.defaultValue(dataType())),
                            Map.entry("isZero", isZero),
                            Map.entry("divByZero", divByZero),
                            Map.entry("leftCode", eval1.code.toString()),
                            Map.entry("checkOverflow", checkIntegralDivideOverflow),
                            Map.entry("operation", operation)
                    )
            ));
        } else {
            String nullOnErrorCondition = failOnError() ? "" : " || " + isZero;
            String failOnErrorBranch = failOnError()
                    ? "if (" + isZero + ") throw QueryExecutionErrors.divideByZeroError(" + errorContextCode + ");"
                    : "";
            return ev.copy(Block.block(
                    """
                            ${rightCode}
                            boolean ${isNull} = false;
                            ${javaType} ${value} = ${defaultValue};
                            if (${rightIsNull}${nullOnErrorCondition}) {
                              ${isNull} = true;
                            } else {
                              ${leftCode}
                              if (${leftIsNull}) {
                                ${isNull} = true;
                              } else {
                                ${failOnErrorBranch}
                                ${checkOverflow}
                                ${operation}
                              }
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("rightCode", eval2.code.toString()),
                            Map.entry("isNull", ev.isNull.toString()),
                            Map.entry("javaType", javaType),
                            Map.entry("value", ev.value.toString()),
                            Map.entry("defaultValue", CodeGeneratorUtils.defaultValue(dataType())),
                            Map.entry("rightIsNull", eval2.isNull.toString()),
                            Map.entry("nullOnErrorCondition", nullOnErrorCondition),
                            Map.entry("leftCode", eval1.code.toString()),
                            Map.entry("leftIsNull", eval1.isNull.toString()),
                            Map.entry("failOnErrorBranch", failOnErrorBranch),
                            Map.entry("checkOverflow", checkIntegralDivideOverflow),
                            Map.entry("operation", operation)
                    )
            ));
        }
    }

    public abstract Object evalOperation(Object left, Object right);
}
