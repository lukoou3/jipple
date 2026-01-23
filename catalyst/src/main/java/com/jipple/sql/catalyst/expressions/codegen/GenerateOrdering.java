package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.expressions.BoundReference;
import com.jipple.sql.catalyst.expressions.SortOrder;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StructField;
import com.jipple.sql.types.StructType;
import com.jipple.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates code for ordering rows for a given schema.
 *
 * Only the comparisons generation is implemented for now.
 */
public final class GenerateOrdering {
    private GenerateOrdering() {
        // Utility class, prevent instantiation
    }

    /**
     * Generates the code for comparing a struct type according to its natural ordering
     * (i.e. ascending order by field 1, then field 2, ..., then field n).
     */
    public static String genComparisons(CodegenContext ctx, StructType schema) {
        StructField[] fields = schema.fields;
        List<SortOrder> ordering = new ArrayList<>(fields.length);
        for (int index = 0; index < fields.length; index++) {
            StructField field = fields[index];
            ordering.add(new SortOrder(
                    new BoundReference(index, field.dataType, true),
                    SortOrder.SortDirection.ASCENDING));
        }
        return genComparisons(ctx, ordering);
    }

    /**
     * Generates the code for ordering based on the given order.
     */
    public static String genComparisons(CodegenContext ctx, List<SortOrder> ordering) {
        String oldInputRow = ctx.INPUT_ROW;
        List<ExprCode> oldCurrentVars = ctx.currentVars;
        List<ExprCode> rowAKeys = createOrderKeys(ctx, "a", ordering);
        List<ExprCode> rowBKeys = createOrderKeys(ctx, "b", ordering);

        List<String> comparisons = new ArrayList<>();
        for (int i = 0; i < ordering.size(); i++) {
            ExprCode l = rowAKeys.get(i);
            ExprCode r = rowBKeys.get(i);
            SortOrder sortOrder = ordering.get(i);
            DataType dt = sortOrder.child.dataType();
            boolean asc = sortOrder.isAscending();
            SortOrder.NullOrdering nullOrdering = sortOrder.nullOrdering;
            String lRetValue = nullOrdering == SortOrder.NullOrdering.NULLS_FIRST ? "-1" : "1";
            String rRetValue = nullOrdering == SortOrder.NullOrdering.NULLS_FIRST ? "1" : "-1";
            String compareCode = CodeGeneratorUtils.template(
                    """
                            ${leftCode}
                            ${rightCode}
                            if (${leftIsNull} && ${rightIsNull}) {
                              // Nothing
                            } else if (${leftIsNull}) {
                              return ${leftRetValue};
                            } else if (${rightIsNull}) {
                              return ${rightRetValue};
                            } else {
                              int comp = ${compareExpr};
                              if (comp != 0) {
                                return ${finalComp};
                              }
                            }
                            """,
                    Map.of(
                            "leftCode", l.code.toString(),
                            "rightCode", r.code.toString(),
                            "leftIsNull", l.isNull.toString(),
                            "rightIsNull", r.isNull.toString(),
                            "leftRetValue", lRetValue,
                            "rightRetValue", rRetValue,
                            "compareExpr", ctx.genComp(dt, l.value.toString(), r.value.toString()),
                            "finalComp", asc ? "comp" : "-comp"
                    )
            );
            comparisons.add(compareCode);
        }

        String code = ctx.splitExpressions(
                comparisons,
                "compare",
                List.of(Tuple2.of("InternalRow", "a"), Tuple2.of("InternalRow", "b")),
                "int",
                body -> CodeGeneratorUtils.template(
                        """
                                ${body}
                                return 0;
                                """,
                        Map.of("body", body)
                ),
                funCalls -> {
                    List<String> folded = new ArrayList<>();
                    for (String funCall : funCalls) {
                        String comp = ctx.freshName("comp");
                        String foldedCall = CodeGeneratorUtils.template(
                                """
                                        int ${comp} = ${funCall};
                                        if (${comp} != 0) {
                                          return ${comp};
                                        }
                                        """,
                                Map.of("comp", comp, "funCall", funCall)
                        );
                        folded.add(foldedCall);
                    }
                    return String.join("", folded);
                }
        );

        ctx.currentVars = oldCurrentVars;
        ctx.INPUT_ROW = oldInputRow;
        return code;
    }

    private static List<ExprCode> createOrderKeys(
            CodegenContext ctx,
            String row,
            List<SortOrder> ordering) {
        ctx.INPUT_ROW = row;
        ctx.currentVars = null;
        List<ExprCode> result = new ArrayList<>(ordering.size());
        for (SortOrder sortOrder : ordering) {
            result.add(sortOrder.child.genCode(ctx));
        }
        return result;
    }
}
