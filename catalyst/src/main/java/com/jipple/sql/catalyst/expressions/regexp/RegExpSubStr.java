package com.jipple.sql.catalyst.expressions.regexp;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.RuntimeReplaceable;
import com.jipple.sql.catalyst.expressions.nvl.NullIf;
import com.jipple.sql.types.AbstractDataType;

import java.util.List;

import static com.jipple.sql.types.DataTypes.STRING;

public class RegExpSubStr extends RuntimeReplaceable {
    public final Expression left;
    public final Expression right;

    public RegExpSubStr(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Expression replacement() {
        return new NullIf(new RegExpExtract(left, right, Literal.of(0)), Literal.of(""));
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(STRING, STRING));
    }

    @Override
    public Object[] args() {
        return new Object[] { left, right };
    }

    @Override
    public final List<Expression> children() {
        return List.of(left, right);
    }

    @Override
    public String prettyName() {
        return "regexp_substr";
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new RegExpSubStr(newChildren.get(0), newChildren.get(1));
    }
}
