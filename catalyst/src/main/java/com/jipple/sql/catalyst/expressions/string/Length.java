package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.BinaryType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.IntegerType;
import com.jipple.sql.types.StringType;
import com.jipple.sql.types.TypeCollection;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

public class Length extends UnaryExpression {
    public Length(Expression child) {
        super(child);
    }

    @Override
    public DataType dataType() {
        return IntegerType.INSTANCE;
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(TypeCollection.of(StringType.INSTANCE, BinaryType.INSTANCE)));
    }

    @Override
    protected Object nullSafeEval(Object value) {
        DataType childType = child.dataType();
        if (childType instanceof StringType) {
            return ((UTF8String) value).numChars();
        }
        if (childType instanceof BinaryType) {
            return ((byte[]) value).length;
        }
        throw new UnsupportedOperationException(childType.sql());
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        DataType childType = child.dataType();
        if (childType instanceof StringType) {
            return defineCodeGen(ctx, ev, c -> "(" + c + ").numChars()");
        }
        if (childType instanceof BinaryType) {
            return defineCodeGen(ctx, ev, c -> "(" + c + ").length");
        }
        throw new UnsupportedOperationException(childType.sql());
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new Length(newChild);
    }
}
