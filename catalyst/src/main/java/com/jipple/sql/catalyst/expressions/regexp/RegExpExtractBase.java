package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.TernaryExpression;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jipple.sql.types.DataTypes.INTEGER;
import static com.jipple.sql.types.DataTypes.STRING;

public abstract class RegExpExtractBase extends TernaryExpression {
    public RegExpExtractBase(Expression subject, Expression regexp, Expression idx) {
        super(subject, regexp, idx);
    }

    public Expression subject() {
        return first;
    }

    public Expression regexp() {
        return second;
    }

    public Expression idx() {
        return third;
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(STRING, STRING, INTEGER));
    }

    // last regex in string, we will update the pattern iff regexp value changed.
    private transient UTF8String lastRegex;
    // last regex pattern, we cache it for performance concern
    private transient Pattern pattern;

    protected Matcher getLastMatcher(Object s, UTF8String p) {
        if (p != lastRegex) {
            // regex value changed
            var patternAndRegex = RegExpUtils.getPatternAndLastRegex(p, prettyName());
            pattern = patternAndRegex._1;
            lastRegex = patternAndRegex._2;
        }
        return pattern.matcher(s.toString());
    }

    public static void checkGroupIndex(String prettyName, int groupCount, int groupIndex) {
        if (groupIndex < 0 || groupCount < groupIndex) {
            throw new IllegalArgumentException("Invalid regex group index: " + groupIndex + " in " + prettyName + ".");
        }
    }

}
