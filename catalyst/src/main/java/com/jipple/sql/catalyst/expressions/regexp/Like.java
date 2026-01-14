package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.util.StringUtils;

import java.util.regex.Pattern;

public class Like extends StringRegexExpression {
    private final char escapeChar;

    public Like(Expression left, Expression right) {
        this(left, right, '\\');
    }

    public Like(Expression left, Expression right, char escapeChar) {
        super(left, right);
        this.escapeChar = escapeChar;
    }

    @Override
    public Object[] args() {
        return new Object[] { left, right, escapeChar };
    }

    @Override
    public String escape(String v) {
        return StringUtils.escapeLikeRegex(v, escapeChar);
    }

    @Override
    public boolean matches(Pattern regex, String str) {
        return regex.matcher(str).matches(); // 完全匹配
    }

    @Override
    public String toString() {
        if (escapeChar == '\\') {
            return String.format("%s LIKE %s", left, right);
        } else {
            return String.format("%s LIKE %s ESCAPE '%s'", left, right, escapeChar);
        }
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new Like(newLeft, newRight, escapeChar);
    }
}
