package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.sql.catalyst.expressions.Expression;

public class UnixMicros extends TimestampToLongBase {
    public UnixMicros(Expression child) {
        super(child);
    }

    @Override
    protected long scaleFactor() {
        return 1L;
    }

    @Override
    public String prettyName() {
        return "unix_micros";
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new UnixMicros(newChild);
    }
}
