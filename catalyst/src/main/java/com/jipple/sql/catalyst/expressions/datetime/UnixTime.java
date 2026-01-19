package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.sql.catalyst.expressions.Expression;

import static com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND;

public abstract class UnixTime extends ToTimestamp {
    public UnixTime(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected long downScaleFactor() {
        return MICROS_PER_SECOND;
    }
}
