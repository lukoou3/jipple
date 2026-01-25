package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.sql.catalyst.expressions.Expression;

import static com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_MILLIS;

public class UnixMillis extends TimestampToLongBase {
    public UnixMillis(Expression child) {
        super(child);
    }

    @Override
    protected long scaleFactor() {
        return MICROS_PER_MILLIS;
    }

    @Override
    public String prettyName() {
        return "unix_millis";
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new UnixMillis(newChild);
    }
}
