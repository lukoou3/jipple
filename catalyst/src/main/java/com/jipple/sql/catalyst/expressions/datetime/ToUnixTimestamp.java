package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.util.TimestampFormatter;

public class ToUnixTimestamp extends UnixTime {
    private final Expression timeExp;
    private final Expression format;
    private final Option<String> timeZoneId;
    public ToUnixTimestamp(Expression timeExp, Expression format, Option<String> timeZoneId) {
        super(timeExp, format);
        this.timeExp = timeExp;
        this.format = format;
        this.timeZoneId = timeZoneId;
    }

    public ToUnixTimestamp(Expression timeExp, Expression format) {
        this(timeExp, format, Option.none());
    }

    public ToUnixTimestamp(Expression timeExp) {
        this(timeExp, Literal.of(TimestampFormatter.defaultPattern()));
    }

    @Override
    public Object[] args() {
        return new Object[] { timeExp, format, timeZoneId };
    }

    @Override
    public Option<String> timeZoneId() {
        return timeZoneId;
    }

    @Override
    public Expression withTimeZone(String timeZoneId) {
        return new ToUnixTimestamp(timeExp, format, Option.of(timeZoneId));
    }

    @Override
    public String prettyName() {
        return "to_unix_timestamp";
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new ToUnixTimestamp(newLeft, newRight, timeZoneId);
    }
}
