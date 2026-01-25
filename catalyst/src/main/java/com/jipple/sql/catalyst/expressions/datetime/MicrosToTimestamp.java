package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.sql.catalyst.expressions.Expression;

public class MicrosToTimestamp extends IntegralToTimestampBase {
    public MicrosToTimestamp(Expression child) {
        super(child);
    }

    @Override
    protected long upScaleFactor() {
        return 1L;
    }

    @Override
    public String prettyName() {
        return "timestamp_micros";
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new MicrosToTimestamp(newChild);
    }
}
