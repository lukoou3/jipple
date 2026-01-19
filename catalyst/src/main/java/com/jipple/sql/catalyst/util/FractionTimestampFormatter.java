package com.jipple.sql.catalyst.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Formatter that omits trailing zeros in fractional seconds.
 */
public class FractionTimestampFormatter extends Iso8601TimestampFormatter {
    private static final long serialVersionUID = 1L;
    private transient DateTimeFormatter fractionFormatter;

    public FractionTimestampFormatter(ZoneId zoneId) {
        super(TimestampFormatter.defaultPattern(), zoneId, TimestampFormatter.DEFAULT_LOCALE);
        this.fractionFormatter = buildFractionFormatter();
    }

    @Override
    protected DateTimeFormatter formatter() {
        return fractionFormatter;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.fractionFormatter = buildFractionFormatter();
    }

    private static DateTimeFormatter buildFractionFormatter() {
        return new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral(' ')
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter(TimestampFormatter.DEFAULT_LOCALE);
    }
}
