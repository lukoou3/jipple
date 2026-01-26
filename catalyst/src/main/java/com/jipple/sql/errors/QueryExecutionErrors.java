package com.jipple.sql.errors;

import com.jipple.error.JippleException;
import com.jipple.error.JippleIllegalArgumentException;
import com.jipple.error.JippleRuntimeException;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.trees.TreeNode;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.array.ByteArrayMethods;

import java.util.Map;
import java.util.stream.Collectors;

import static com.jipple.sql.catalyst.util.JippleStringUtils.sideBySide;
import static com.jipple.sql.types.DataTypes.NULL;

public class QueryExecutionErrors {

    public static JippleException cannotEvaluateExpressionError(Expression expression) {
        return  JippleException.internalError("Cannot evaluate expression: " + expression);
    }

    public static JippleException cannotGenerateCodeForExpressionError(Expression expression) {
        return  JippleException.internalError("Cannot generate code for expression: " + expression);
    }

    public static JippleRuntimeException notOverrideExpectedMethodsError(String className, String m1, String m2) {
        return new JippleRuntimeException(
                "_LEGACY_ERROR_TEMP_2025",
                Map.of("className", className, "m1", m1, "m2", m2)
        );
    }

    public static JippleException cannotCastFromNullTypeError(DataType to) {
        return new JippleException(
                "CANNOT_CAST_DATATYPE",
                Map.of("sourceType", NULL.typeName(), "targetType", to.typeName()),
                null);
    }

    public static <T extends TreeNode<?>> JippleRuntimeException onceStrategyIdempotenceIsBrokenForBatchError(
            String batchName, T plan, T reOptimized) {
        return new JippleRuntimeException(
                "_LEGACY_ERROR_TEMP_2172",
                Map.of("batch", batchName, "plan", sideBySide(plan.treeString(), reOptimized.treeString()).stream().collect(Collectors.joining("\n")))
                );
    }

    public static JippleIllegalArgumentException tooManyArrayElementsError(int numElements, int elementSize) {
        return new JippleIllegalArgumentException(
                "TOO_MANY_ARRAY_ELEMENTS",
                Map.of("numElements", String.valueOf(numElements), "size",  String.valueOf(elementSize))
        );
    }

    public static JippleIllegalArgumentException concatArraysWithElementsExceedLimitError(long numElements) {
        return new JippleIllegalArgumentException(
                "_LEGACY_ERROR_TEMP_2159",
                Map.of(
                    "numberOfElements", String.valueOf(numElements),
                "maxRoundedArrayLength", String.valueOf(ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH))
        );
    }

    public static JippleException divideByZeroError(String errorContext) {
        String msg = "Division by zero";
        if (errorContext != null) {
            msg = msg + ": " + errorContext;
        }
        return JippleException.internalError(msg);
    }

    public static JippleException overflowInIntegralDivideError(String errorContext) {
        String msg = "Integral divide overflow";
        if (errorContext != null) {
            msg = msg + ": " + errorContext;
        }
        return JippleException.internalError(msg);
    }

    public static JippleRuntimeException unreachableError(String err){
        return new JippleRuntimeException("_LEGACY_ERROR_TEMP_2028", Map.of("err", err));
    }

    public static JippleRuntimeException invalidPatternError(String funcName, String pattern, Throwable cause){
        return new JippleRuntimeException("INVALID_PARAMETER_VALUE.PATTERN",
                Map.of(
                        "parameter", "regexp",
                        "functionName", funcName,
                        "value", pattern
                ),
                cause
                );
    }
}
