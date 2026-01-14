package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.sql.catalyst.expressions.Expression;

import java.util.regex.Pattern;

public class RLike extends StringRegexExpression {
    public RLike(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String escape(String v) {
        return v;
    }

    @Override
    public boolean matches(Pattern regex, String str) {
        return regex.matcher(str).find(0); // 包含匹配
    }

    @Override
    public String toString() {
        return String.format("RLIKE(%s, %s)", left, right);
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new RLike(newLeft, newRight);
    }
}
