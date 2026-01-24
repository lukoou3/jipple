package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.LeafExpression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.CodegenFallback;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.DataType;

import static com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_MILLIS;
import static com.jipple.sql.types.DataTypes.TIMESTAMP;

public abstract class CurrentTimestampLike extends LeafExpression implements CodegenFallback {
    @Override
    public final boolean foldable() {
        return false; // 主要用于实时场景
    }

    @Override
    public final boolean nullable() {
        return false;
    }

    @Override
    public final DataType dataType() {
        return TIMESTAMP;
    }

    @Override
    public final Object eval(InternalRow input) {
        return System.currentTimeMillis() * MICROS_PER_MILLIS;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        return CodegenFallback.doGenCode(this, ctx, ev);
    }
}
