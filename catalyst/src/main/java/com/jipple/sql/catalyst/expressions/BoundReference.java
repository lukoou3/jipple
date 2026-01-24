package com.jipple.sql.catalyst.expressions;

import com.google.common.base.Preconditions;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.InternalRowAccessor;
import com.jipple.sql.catalyst.expressions.codegen.*;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.types.DataType;

import java.util.Map;

public class BoundReference extends LeafExpression {
    public final int ordinal;
    public final DataType dataType;
    public final boolean nullable;
    private final InternalRowAccessor accessor;

    public BoundReference(int ordinal, DataType dataType) {
        this(ordinal, dataType, true);
    }

    public BoundReference(int ordinal, DataType dataType, boolean nullable) {
        this.ordinal = ordinal;
        this.dataType = dataType;
        this.nullable = nullable;
        this.accessor = InternalRow.getAccessor(dataType, nullable);
    }

    @Override
    public String toString() {
        return "input[" + ordinal + ", " + dataType.simpleString() + ", " + nullable + "]";
    }

    @Override
    public Object[] args() {
        return new Object[]{ordinal, dataType, nullable};
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

    @Override
    public DataType dataType() {
        return dataType;
    }

    @Override
    public Object eval(InternalRow input) {
        return accessor.get(input, ordinal);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        if (ctx.currentVars != null  && ctx.currentVars.get(ordinal) != null) {
            ExprCode oev = ctx.currentVars.get(ordinal);
            ev.isNull = oev.isNull;
            ev.value = oev.value;
            return ev.copy(oev.code);
        } else {
            Preconditions.checkArgument(ctx.INPUT_ROW != null, "INPUT_ROW and currentVars cannot both be null.");
            String javaType = JavaCode.javaType(dataType).code();
            String value = CodeGeneratorUtils.getValue(ctx.INPUT_ROW, dataType, String.valueOf(ordinal));
            if (nullable) {
                return ev.copy(Block.block(
                        """
                                boolean ${isNull} = ${inputRow}.isNullAt(${ordinal});
                                ${javaType} ${value} = ${isNull} ? ${defaultValue} : (${getValue});
                                """,
                        Map.of(
                                "isNull", ev.isNull,
                                "inputRow", ctx.INPUT_ROW,
                                "ordinal", ordinal,
                                "javaType", javaType,
                                "value", ev.value,
                                "defaultValue", CodeGeneratorUtils.defaultValue(dataType()),
                                "getValue", value
                        )
                ));
            } else {
                return ev.copy(
                        Block.block(
                                "${javaType} ${value} = ${getValue};",
                                Map.of(
                                        "javaType", javaType,
                                        "value", ev.value,
                                        "getValue", value
                                )
                        ),
                        FalseLiteral.INSTANCE,
                        null
                );
            }
        }
    }
}
