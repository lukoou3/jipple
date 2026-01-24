package com.jipple.sql.catalyst.expressions.string;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

public class StringTrim extends String2TrimExpression {

    public StringTrim(Expression srcStr, Option<Expression> trimStr) {
        super(srcStr, trimStr);
    }

    public StringTrim(Expression srcStr, Expression trimStr) {
        this(srcStr, Option.option(trimStr));
    }

    public StringTrim(Expression srcStr) {
        this(srcStr, Option.none());
    }

    @Override
    public Object[] args() {
        return new Object[] {srcStr, trimStr};
    }

    @Override
    public String prettyName() {
        return "trim";
    }

    @Override
    protected UTF8String doEval(UTF8String srcString) {
        return srcString.trim();
    }

    @Override
    protected UTF8String doEval(UTF8String srcString, UTF8String trimString) {
        return srcString.trim(trimString);
    }

    @Override
    protected String trimMethod() {
        return "trim";
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new StringTrim(newChildren.get(0), trimStr.isDefined() ? Option.option(newChildren.get(1)) : Option.none());
    }

}
