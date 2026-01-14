package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.BinaryExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;
import java.util.regex.Pattern;

import static com.jipple.sql.types.DataTypes.*;

public abstract class StringRegexExpression extends BinaryExpression {
    private Pattern cache;

    public StringRegexExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.of(List.of(STRING, STRING));
    }

    public abstract String escape(String v);

    public abstract boolean matches(Pattern regex, String str);

    protected Pattern compile(String str) {
        if (str == null) {
            return null;
        } else {
            // Let it raise exception if couldn't compile the regex string
            return Pattern.compile(escape(str));
        }
    }

    protected Pattern pattern(UTF8String str) {
        if (cache == null) {
            if (right.foldable()) {
                cache = compile(str.toString());
                return cache;
            }
            return compile(str.toString());
        } else {
            return cache;
        }
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    @Override
    protected Object nullSafeEval(Object input1, Object input2) {
        Pattern regex = pattern((UTF8String) input2);
        if (regex == null) {
            return null;
        } else {
            return matches(regex, ((UTF8String) input1).toString());
        }
    }
}
