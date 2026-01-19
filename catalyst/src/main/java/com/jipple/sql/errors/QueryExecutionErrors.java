package com.jipple.sql.errors;

import com.jipple.error.JippleException;
import com.jipple.error.JippleRuntimeException;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.trees.TreeNode;
import com.jipple.sql.types.DataType;

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
}
