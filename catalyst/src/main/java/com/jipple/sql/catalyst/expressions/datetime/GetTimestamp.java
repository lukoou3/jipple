package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.TimestampNTZType;

public class GetTimestamp extends ToTimestamp {
    private final Option<String> timeZoneId;
    private final DataType dataType;

    public GetTimestamp(Expression left, Expression right, DataType dataType, Option<String> timeZoneId) {
        super(left, right);
        this.dataType = dataType;
        this.timeZoneId = timeZoneId;
    }

    @Override
    public Object[] args() {
        return new Object[]{left, right, dataType, timeZoneId};
    }

    @Override
    public Option<String> timeZoneId() {
        return timeZoneId;
    }

    @Override
    public Expression withTimeZone(String timeZoneId) {
        return new GetTimestamp(left, right, dataType, Option.of(timeZoneId));
    }

    @Override
    protected long downScaleFactor() {
        return 1;
    }

    @Override
    protected boolean forTimestampNTZ() {
        return dataType instanceof TimestampNTZType;
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new GetTimestamp(newLeft, newRight, dataType, timeZoneId);
    }
}
