package com.jipple.sql.catalyst.expressions.complextype;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StructType;
import java.util.Map;

public class GetStructField extends UnaryExpression {
    public final int ordinal;
    public final Option<String> name;
    private StructType _childSchema;

    public GetStructField(Expression child, int ordinal) {
        this(child, ordinal, Option.empty());
    }

    public GetStructField(Expression child, int ordinal, Option<String> name) {
        super(child);
        this.ordinal = ordinal;
        this.name = name;
    }

    @Override
    public Object[] args() {
        return new Object[]{child, ordinal, name};
    }

    public StructType childSchema() {
        if (_childSchema == null) {
            _childSchema = (StructType) child.dataType();
        }
        return _childSchema;
    }

    @Override
    public DataType dataType() {
        return childSchema().fields[ordinal].dataType;
    }

    @Override
    public boolean nullable() {
        return child.nullable() || childSchema().fields[ordinal].nullable;
    }

    public String extractFieldName() {
        return name.isDefined() ? name.get() : childSchema().fields[ordinal].name;
    }

    @Override
    public String sql() {
        return child.sql() + "." + extractFieldName();
    }

    @Override
    protected Object nullSafeEval(Object input) {
        return ((InternalRow) input).get(ordinal, childSchema().fields[ordinal].dataType);
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return nullSafeCodeGen(ctx, ev, eval -> {
            if (nullable()) {
                return CodeGeneratorUtils.template(
                        """
                                if (${eval}.isNullAt(${ordinal})) {
                                  ${isNull} = true;
                                } else {
                                  ${value} = ${getValue};
                                }
                                """,
                        Map.of(
                                "eval", eval,
                                "ordinal", ordinal,
                                "isNull", ev.isNull,
                                "value", ev.value,
                                "getValue", CodeGeneratorUtils.getValue(eval, dataType(), String.valueOf(ordinal))
                        )
                );
            } else {
                return CodeGeneratorUtils.template(
                        """
                                ${value} = ${getValue};
                                """,
                        Map.of(
                                "value", ev.value,
                                "getValue", CodeGeneratorUtils.getValue(eval, dataType(), String.valueOf(ordinal))
                        )
                );
            }
        });
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new GetStructField(newChild, ordinal, name);
    }
}
