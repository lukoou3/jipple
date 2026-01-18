package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.util.TimestampFormatter;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

import static com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND;
import static com.jipple.sql.types.DataTypes.LONG;
import static com.jipple.sql.types.DataTypes.STRING;

public class FromUnixTime extends TimestampFormatterHelper {
    private final Expression sec;
    private final Expression format;
    private final Option<String> timeZoneId;

    public FromUnixTime(Expression sec, Expression format, Option<String> timeZoneId) {
        super(sec, format);
        this.sec = sec;
        this.format = format;
        this.timeZoneId = timeZoneId;
    }

    public FromUnixTime(Expression sec, Expression format) {
        this(sec, format, Option.none());
    }

    @Override
    public Option<String> timeZoneId() {
        return timeZoneId;
    }

    @Override
    public FromUnixTime withTimeZone(String timeZoneId) {
        return new FromUnixTime(sec, format, Option.of(timeZoneId));
    }

    @Override
    protected Expression formatString() {
        return format;
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    public boolean nullable() {
        return true;
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(LONG, STRING));
    }

    @Override
    protected Object nullSafeEval(Object seconds, Object format) {
        Option<TimestampFormatter> timestampFormatterOption = formatterOption();
        TimestampFormatter fmt = timestampFormatterOption.isDefined() ? timestampFormatterOption.get() : getFormatter(format.toString());
        return UTF8String.fromString(fmt.format((Long) seconds * MICROS_PER_SECOND));
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new FromUnixTime(newLeft, newRight, timeZoneId);
    }
}
