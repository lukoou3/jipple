package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

public class StringTrimLeft extends String2TrimExpression {

    public StringTrimLeft(Expression srcStr, Option<Expression> trimStr) {
        super(srcStr, trimStr);
    }

    public StringTrimLeft(Expression srcStr, Expression trimStr) {
        this(srcStr, Option.option(trimStr));
    }

    public StringTrimLeft(Expression srcStr) {
        this(srcStr, Option.none());
    }

    @Override
    public Object[] args() {
        return new Object[] {srcStr, trimStr};
    }

    @Override
    public String prettyName() {
        return "ltrim";
    }

    @Override
    protected UTF8String doEval(UTF8String srcString) {
        return srcString.trimLeft();
    }

    @Override
    protected UTF8String doEval(UTF8String srcString, UTF8String trimString) {
        return srcString.trimLeft(trimString);
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new StringTrimLeft(newChildren.get(0), trimStr.isDefined() ? Option.option(newChildren.get(1)) : Option.none());
    }

}
