package com.jipple.sql.catalyst.expressions.complextype;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.types.DataType;
import com.jipple.tuple.Tuple2;
import com.jipple.tuple.Tuple3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for generating code that creates ArrayData.
 */
public final class GenArrayData {
    private GenArrayData() {
    }

    /**
     * Return Java code pieces based on DataType and array size to allocate ArrayData class.
     *
     * @param ctx a {@link CodegenContext}
     * @param elementType data type of underlying array elements
     * @param elementsExpr concatenated set of {@link Expression} for each element of an underlying array
     * @param functionName string to include in the error message
     * @return (array allocation, concatenated assignments to each array elements, arrayData name)
     */
    public static Tuple3<String, String, String> genCodeToCreateArrayData(
            CodegenContext ctx,
            DataType elementType,
            List<Expression> elementsExpr,
            String functionName) {
        String arrayDataName = ctx.freshName("arrayData");
        String numElements = elementsExpr.size() + "L";

        String initialization = CodeGeneratorUtils.createArrayData(
                arrayDataName,
                elementType,
                numElements,
                " " + functionName + " failed."
        );

        List<String> assignments = new ArrayList<>(elementsExpr.size());
        for (int i = 0; i < elementsExpr.size(); i++) {
            Expression expr = elementsExpr.get(i);
            ExprCode eval = expr.genCode(ctx);
            String setArrayElement = CodeGeneratorUtils.setArrayElement(
                    arrayDataName,
                    elementType,
                    String.valueOf(i),
                    eval.value.toString()
            );
            String assignment = !expr.nullable()
                    ? setArrayElement
                    : CodeGeneratorUtils.template(
                            """
                                    if (${isNull}) {
                                      ${arrayData}.setNullAt(${index});
                                    } else {
                                      ${setArrayElement}
                                    }
                                    """,
                            Map.ofEntries(
                                    Map.entry("isNull", eval.isNull),
                                    Map.entry("arrayData", arrayDataName),
                                    Map.entry("index", i),
                                    Map.entry("setArrayElement", setArrayElement)
                            )
                    );
            String code = CodeGeneratorUtils.template(
                    """
                            ${evalCode}
                            ${assignment}
                            """,
                    Map.ofEntries(
                            Map.entry("evalCode", eval.code),
                            Map.entry("assignment", assignment)
                    )
            );
            assignments.add(code);
        }

        String assignmentString = ctx.splitExpressionsWithCurrentInputs(
                assignments,
                "apply",
                List.of(Tuple2.of(ArrayData.class.getName(), arrayDataName))
        );

        return Tuple3.of(initialization, assignmentString, arrayDataName);
    }
}
