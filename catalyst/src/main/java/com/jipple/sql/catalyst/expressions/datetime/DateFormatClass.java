package com.jipple.sql.catalyst.expressions.datetime;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.TimestampFormatter;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.unsafe.types.UTF8String;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

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
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        Option<TimestampFormatter> timestampFormatterOption = formatterOption();
        if (timestampFormatterOption.isDefined()) {
            String timestampFormatter = ctx.addReferenceObj("timestampFormatter", timestampFormatterOption.get());
            return defineCodeGen(ctx, ev, (timestamp, format) -> CodeGeneratorUtils.template(
                    "UTF8String.fromString(${formatter}.format(${timestamp}))",
                    Map.of(
                            "formatter", timestampFormatter,
                            "timestamp", timestamp
                    )
            ));
        }
        String zoneId = ctx.addReferenceObj("zoneId", zoneId(), ZoneId.class.getName());
        String timestampFormatterClass = TimestampFormatter.class.getName();
        return defineCodeGen(ctx, ev, (timestamp, format) -> CodeGeneratorUtils.template(
                """
                        UTF8String.fromString(
                          ${timestampFormatter}.getFormatter(${format}.toString(), ${zoneId})
                          .format(${timestamp}))
                        """,
                Map.ofEntries(
                        Map.entry("timestampFormatter", timestampFormatterClass),
                        Map.entry("format", format),
                        Map.entry("zoneId", zoneId),
                        Map.entry("timestamp", timestamp)
                )
        ));
    }

    @Override
    public Expression withNewChildInternal(Expression newLeft, Expression newRight) {
        return new DateFormatClass(newLeft, newRight, timeZoneId);
    }
}
