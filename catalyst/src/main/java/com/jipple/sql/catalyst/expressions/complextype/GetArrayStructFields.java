package com.jipple.sql.catalyst.expressions.complextype;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.GenericArrayData;
import com.jipple.sql.catalyst.util.QuotingUtils;
import com.jipple.sql.types.ArrayType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StructField;

import java.util.Map;

public class GetArrayStructFields extends UnaryExpression {
    public final StructField field;
    public final int ordinal;
    public final int numFields;
    public final boolean containsNull;

    public GetArrayStructFields(
            Expression child,
            StructField field,
            int ordinal,
            int numFields,
            boolean containsNull) {
        super(child);
        this.field = field;
        this.ordinal = ordinal;
        this.numFields = numFields;
        this.containsNull = containsNull;
    }

    @Override
    public Object[] args() {
        return new Object[]{child, field, ordinal, numFields, containsNull};
    }

    @Override
    public DataType dataType() {
        return new ArrayType(field.dataType, containsNull);
    }

    @Override
    public String toString() {
        return child + "." + field.name;
    }

    @Override
    public String sql() {
        return child.sql() + "." + QuotingUtils.quoteIdentifier(field.name);
    }

    @Override
    protected Object nullSafeEval(Object input) {
        ArrayData array = (ArrayData) input;
        int length = array.numElements();
        Object[] result = new Object[length];
        for (int i = 0; i < length; i++) {
            if (array.isNullAt(i)) {
                result[i] = null;
            } else {
                InternalRow row = array.getStruct(i, numFields);
                if (row.isNullAt(ordinal)) {
                    result[i] = null;
                } else {
                    result[i] = row.get(ordinal, field.dataType);
                }
            }
        }
        return new GenericArrayData(result);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String arrayClass = GenericArrayData.class.getName();
        return nullSafeCodeGen(ctx, ev, eval -> {
            String n = ctx.freshName("n");
            String values = ctx.freshName("values");
            String j = ctx.freshName("j");
            String row = ctx.freshName("row");
            String nullSafeEval = field.nullable
                    ? CodeGeneratorUtils.template(
                            """
                                    if (${row}.isNullAt(${ordinal})) {
                                      ${values}[${j}] = null;
                                    } else
                                    """,
                            Map.of(
                                    "row", row,
                                    "ordinal", ordinal,
                                    "values", values,
                                    "j", j
                            )
                    )
                    : "";
            return CodeGeneratorUtils.template(
                    """
                            final int ${n} = ${eval}.numElements();
                            final Object[] ${values} = new Object[${n}];
                            for (int ${j} = 0; ${j} < ${n}; ${j}++) {
                              if (${eval}.isNullAt(${j})) {
                                ${values}[${j}] = null;
                              } else {
                                final InternalRow ${row} = ${eval}.getStruct(${j}, ${numFields});
                                ${nullSafeEval} {
                                  ${values}[${j}] = ${getValue};
                                }
                              }
                            }
                            ${value} = new ${arrayClass}(${values});
                            """,
                    Map.ofEntries(
                            Map.entry("n", n),
                            Map.entry("eval", eval),
                            Map.entry("values", values),
                            Map.entry("j", j),
                            Map.entry("row", row),
                            Map.entry("numFields", numFields),
                            Map.entry("nullSafeEval", nullSafeEval),
                            Map.entry("value", ev.value),
                            Map.entry("arrayClass", arrayClass),
                            Map.entry("getValue", CodeGeneratorUtils.getValue(row, field.dataType, String.valueOf(ordinal)))
                    )
            );
        });
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new GetArrayStructFields(newChild, field, ordinal, numFields, containsNull);
    }
}
