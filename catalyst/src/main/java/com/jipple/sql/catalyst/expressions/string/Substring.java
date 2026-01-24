package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.TernaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.BinaryType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StringType;
import com.jipple.sql.types.TypeCollection;
import com.jipple.unsafe.types.ByteArray;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

import static com.jipple.sql.types.DataTypes.*;

public class Substring extends TernaryExpression {
    public Substring(Expression str, Expression pos, Expression len) {
        super(str, pos, len);
    }

    public Substring(Expression str, Expression pos) {
        this(str, pos, Literal.of(Integer.MAX_VALUE));
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.of(List.of(TypeCollection.of(STRING, BINARY), INTEGER, INTEGER));
    }

    @Override
    public DataType dataType() {
        return first.dataType();
    }

    @Override
    protected Object nullSafeEval(Object string, Object pos, Object len) {
        if (first.dataType() instanceof StringType) {
            return ((UTF8String) string).substringSQL((Integer) pos, (Integer) len);
        } else {
            return ByteArray.subStringSQL((byte[]) string, (Integer) pos, (Integer) len);
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return defineCodeGen(ctx, ev, (string, pos, len) -> {
            if (first.dataType() instanceof StringType) {
                return string + ".substringSQL(" + pos + ", " + len + ")";
            } else if (first.dataType() instanceof BinaryType) {
                return ByteArray.class.getName() + ".subStringSQL(" + string + ", " + pos + ", " + len + ")";
            } else {
                throw new UnsupportedOperationException("Unsupported data type: " + first.dataType());
            }
        });
    }

    @Override
    public Expression withNewChildInternal(Expression newFirst, Expression newSecond, Expression newThird) {
        return new Substring(newFirst, newSecond, newThird);
    }

}
