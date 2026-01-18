package com.jipple.sql.catalyst.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Formatter that omits trailing zeros in fractional seconds.
 */
public class FractionTimestampFormatter extends Iso8601TimestampFormatter {
    private final DateTimeFormatter fractionFormatter;

    public FractionTimestampFormatter(ZoneId zoneId) {
        super(TimestampFormatter.defaultPattern(), zoneId, TimestampFormatter.DEFAULT_LOCALE);
        this.fractionFormatter = new DateTimeFormatterBuilder()
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

    @Override
    protected DateTimeFormatter formatter() {
        return fractionFormatter;
    }
}
