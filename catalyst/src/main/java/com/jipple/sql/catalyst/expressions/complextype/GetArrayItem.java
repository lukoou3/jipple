package com.jipple.sql.catalyst.expressions.complextype;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.BinaryExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.*;
import com.jipple.sql.catalyst.util.ArrayData;

import java.util.List;
import java.util.function.BiFunction;

import static com.jipple.sql.types.DataTypes.ANY;
import static com.jipple.sql.types.DataTypes.INTEGER;

public class GetArrayItem extends BinaryExpression {
    public GetArrayItem(Expression child, Expression ordinal) {
        super(child, ordinal);
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        // We have done type checking for child in `ExtractValue`, so only need to check the `ordinal`.
        return Option.some(List.of(ANY, INTEGER));
    }

    @Override
    public DataType dataType() {
        return ((ArrayType) left.dataType()).elementType;
    }

    @Override
    public boolean nullable() {
        return computeNullabilityFromArray(left, right, GetArrayItem::nullability);
    }

    @Override
    public String toString() {
        return left + "[" + right + "]";
    }

    @Override
    public String sql() {
        return left.sql() + "[" + right.sql() + "]";
    }

    @Override
    protected Object nullSafeEval(Object value, Object ordinal) {
        ArrayData baseValue = (ArrayData) value;
        int index = ((Number) ordinal).intValue();
        if (index >= baseValue.numElements() || index < 0) {
            return null;
        } else if (baseValue.isNullAt(index)) {
            return null;
        } else {
            return baseValue.get(index, dataType());
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return nullSafeCodeGen(ctx, ev, (eval1, eval2) -> {
            String index = ctx.freshName("index");
            boolean childArrayElementNullable = ((ArrayType) left.dataType()).containsNull;
            String nullCheck = childArrayElementNullable
                    ? CodeGeneratorUtils.template(
                            """
                                    else if (${array}.isNullAt(${index})) {
                                      ${isNull} = true;
                                    }
                                    """,
                            java.util.Map.of(
                                    "array", eval1,
                                    "index", index,
                                    "isNull", ev.isNull
                            )
                    )
                    : "";
            return CodeGeneratorUtils.template(
                    """
                            final int ${index} = (int) ${ordinal};
                            if (${index} >= ${array}.numElements() || ${index} < 0) {
                              ${isNull} = true;
                            } ${nullCheck} else {
                              ${value} = ${getValue};
                            }
                            """,
                    java.util.Map.ofEntries(
                            java.util.Map.entry("index", index),
                            java.util.Map.entry("ordinal", eval2),
                            java.util.Map.entry("array", eval1),
                            java.util.Map.entry("nullCheck", nullCheck),
                            java.util.Map.entry("value", ev.value),
                            java.util.Map.entry("getValue",
                                    CodeGeneratorUtils.getValue(eval1, dataType(), index))
                    )
            );
        });
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new GetArrayItem(newLeft, newRight);
    }

    private static boolean nullability(List<Expression> elements, Integer ordinal) {
        if (ordinal >= 0 && ordinal < elements.size()) {
            return elements.get(ordinal).nullable();
        }
        return true;
    }

    /**
     * `Null` is returned for invalid ordinals.
     */
    public static boolean computeNullabilityFromArray(
            Expression child,
            Expression ordinal,
            BiFunction<List<Expression>, Integer, Boolean> nullability) {
        if (ordinal.foldable() && !ordinal.nullable()) {
            int intOrdinal = ((Number) ordinal.eval()).intValue();
            if (child instanceof CreateArray createArray) {
                return nullability.apply(createArray.children(), intOrdinal);
            }
            if (child instanceof GetArrayStructFields getArrayStructFields
                    && getArrayStructFields.child instanceof CreateArray createArray) {
                boolean baseNullability = nullability.apply(createArray.children(), intOrdinal);
                return getArrayStructFields.field.nullable || baseNullability;
            }
            return true;
        } else {
            return true;
        }
    }

}
