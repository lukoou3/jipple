package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.TimeZoneAwareExpression;
import com.jipple.sql.catalyst.util.JippleDateTimeUtils;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.TimestampType;

import java.time.ZoneId;
import java.util.List;

import static com.jipple.sql.types.DataTypes.STRING;
import static com.jipple.sql.types.DataTypes.TIMESTAMP;

public class TruncTimestamp extends TruncInstant implements TimeZoneAwareExpression {
    private final Expression format;
    private final Expression timestamp;
    private final Option<String> timeZoneId;

    public TruncTimestamp(Expression format, Expression timestamp, Option<String> timeZoneId) {
        super(format, timestamp);
        this.format = format;
        this.timestamp = timestamp;
        this.timeZoneId = timeZoneId;
    }

    public TruncTimestamp(Expression format, Expression timestamp) {
        this(format, timestamp, Option.none());
    }

    @Override
    public Object[] args() {
        return new Object[] { format, timestamp, timeZoneId };
    }

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        return Option.some(List.of(STRING, TIMESTAMP));
    }

    @Override
    public DataType dataType() {
        return TimestampType.INSTANCE;
    }

    @Override
    public String prettyName() {
        return "date_trunc";
    }

    @Override
    protected Expression instant() {
        return timestamp;
    }

    @Override
    protected Expression format() {
        return format;
    }

    @Override
    public Option<String> timeZoneId() {
        return timeZoneId;
    }

    @Override
    public Expression withTimeZone(String timeZoneId) {
        return new TruncTimestamp(format, timestamp, Option.of(timeZoneId));
    }

    @Override
    public Object eval(InternalRow input) {
        return evalHelper(input, JippleDateTimeUtils.MIN_LEVEL_OF_TIMESTAMP_TRUNC,
                (t, level) -> JippleDateTimeUtils.truncTimestamp((Long) t, level, zoneId()));
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String zoneId = ctx.addReferenceObj("zoneId", zoneId(), ZoneId.class.getName());
        return codeGenHelper(ctx, ev, JippleDateTimeUtils.MIN_LEVEL_OF_TIMESTAMP_TRUNC, true,
                (t, fmt) -> "truncTimestamp(" + t + ", " + fmt + ", " + zoneId + ")");
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new TruncTimestamp(newLeft, newRight, timeZoneId);
    }
}
