package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

import static com.jipple.sql.types.DataTypes.STRING;

public abstract class String2TrimExpression extends Expression {
    protected final Expression srcStr;
    protected final Option<Expression> trimStr;

    public String2TrimExpression(Expression srcStr, Option<Expression> trimStr) {
        this.srcStr = srcStr;
        this.trimStr = trimStr;
    }

    @Override
    public List<Expression> children() {
        return trimStr.isDefined() ? List.of(srcStr, trimStr.get()) : List.of(srcStr);
    }

    @Override
    public boolean nullable() {
        return children().stream().anyMatch(x -> x.nullable());
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    public boolean foldable() {
        return children().stream().allMatch(x -> x.foldable());
    }

    protected abstract UTF8String doEval(UTF8String srcString);

    protected abstract UTF8String doEval(UTF8String srcString, UTF8String trimString);

    @Override
    public Object eval(InternalRow input) {
        UTF8String srcString = (UTF8String)srcStr.eval(input);
        if (srcString == null) {
            return null;
        } else if (trimStr.isDefined()) {
            UTF8String trimString = (UTF8String)trimStr.get().eval(input);
            return doEval(srcString, trimString);
        } else {
            return doEval(srcString);
        }
    }

}
