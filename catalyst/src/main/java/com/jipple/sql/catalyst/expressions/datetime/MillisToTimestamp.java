package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.sql.catalyst.expressions.Expression;

import static com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_MILLIS;

public class MillisToTimestamp extends IntegralToTimestampBase {
    public MillisToTimestamp(Expression child) {
        super(child);
    }

    @Override
    protected long upScaleFactor() {
        return MICROS_PER_MILLIS;
    }

    @Override
    public String prettyName() {
        return "timestamp_millis";
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new MillisToTimestamp(newChild);
    }
}
