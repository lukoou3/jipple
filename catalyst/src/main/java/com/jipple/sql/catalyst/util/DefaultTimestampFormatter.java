package com.jipple.sql.catalyst.util;

import java.time.ZoneId;
import java.util.Locale;

/**
 * The formatter for timestamps which doesn't require users to specify a pattern.
 * It uses the default pattern {@link TimestampFormatter#defaultPattern()}.
 */
public class DefaultTimestampFormatter extends Iso8601TimestampFormatter {
    public DefaultTimestampFormatter(ZoneId zoneId, Locale locale) {
        super(TimestampFormatter.defaultPattern(), zoneId, locale);
    }

    public DefaultTimestampFormatter(ZoneId zoneId) {
        this(zoneId, TimestampFormatter.DEFAULT_LOCALE);
    }
}
