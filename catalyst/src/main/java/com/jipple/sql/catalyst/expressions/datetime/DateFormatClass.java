package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.util.TimestampFormatter;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.util.List;

import static com.jipple.sql.types.DataTypes.TIMESTAMP;
import static com.jipple.sql.types.DataTypes.STRING;

public class DateFormatClass extends TimestampFormatterHelper {
    private final Option<String> timeZoneId;

    public DateFormatClass(Expression left, Expression right, Option<String> timeZoneId) {
        super(left, right);
        this.timeZoneId = timeZoneId;
    }

    public DateFormatClass(Expression left, Expression right) {
        this(left, right, Option.none());
    }

    @Override
    public Object[] args() {
        return new Object[] { left, right, timeZoneId };
    }

    @Override
    public Option<String> timeZoneId() {
        return timeZoneId;
    }

    @Override
    public DateFormatClass withTimeZone(String timeZoneId) {
        return new DateFormatClass(left, right, Option.of(timeZoneId));
    }

    @Override
    protected Expression formatString() {
        return right;
    }

    @Override
    public DataType dataType() {
        return STRING;
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(TIMESTAMP, STRING));
    }

    @Override
    public String prettyName() {
        return "date_format";
    }

    @Override
    protected Object nullSafeEval(Object timestamp, Object format) {
        Option<TimestampFormatter> timestampFormatterOption = formatterOption();
        TimestampFormatter fmt = timestampFormatterOption.isDefined() ? timestampFormatterOption.get() : getFormatter(format.toString());
        return UTF8String.fromString(fmt.format((Long) timestamp));
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new DateFormatClass(newLeft, newRight, timeZoneId);
    }
}
