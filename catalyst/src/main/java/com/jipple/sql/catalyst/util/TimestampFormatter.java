package com.jipple.sql.catalyst.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import com.jipple.collection.Option;

public interface TimestampFormatter {
    Locale DEFAULT_LOCALE = Locale.US;

    /**
     * Parses a timestamp in a string and converts it to microseconds.
     */
    long parse(String s) throws DateTimeParseException;

    /**
     * Parses a timestamp in a string and converts it to an optional number of microseconds.
     */
    default Option<Long> parseOptional(String s) {
        try {
            return Option.option(parse(s));
        } catch (Exception ignored) {
            return Option.none();
        }
    }

    /**
     * Parses a timestamp in a string and converts it to microseconds since Unix Epoch in local time.
     */
    default long parseWithoutTimeZone(String s, boolean allowTimeZone)
            throws DateTimeParseException {
        throw new IllegalStateException(
                "parseWithoutTimeZone(s, allowTimeZone) should be implemented for timestamp without time zone");
    }

    /**
     * Parses a timestamp in a string and converts it to an optional number of microseconds
     * since Unix Epoch in local time.
     */
    default Option<Long> parseWithoutTimeZoneOptional(String s, boolean allowTimeZone) {
        try {
            return Option.option(parseWithoutTimeZone(s, allowTimeZone));
        } catch (Exception ignored) {
            return Option.none();
        }
    }

    /**
     * Parses a timestamp in a string and converts it to microseconds since Unix Epoch in local time.
     * Zone-id and zone-offset components are ignored.
     */
    default long parseWithoutTimeZone(String s)
            throws DateTimeParseException {
        return parseWithoutTimeZone(s, true);
    }

    String format(long us);

    String format(Timestamp ts);

    String format(Instant instant);

    default String format(LocalDateTime localDateTime) {
        throw new IllegalStateException(
                "format(localDateTime) should be implemented for timestamp without time zone");
    }

    /**
     * Validates the pattern string.
     */
    void validatePatternString(boolean checkLegacy);

    static String defaultPattern() {
        return "yyyy-MM-dd HH:mm:ss";
    }

    static TimestampFormatter getFormatter(Option<String> format, ZoneId zoneId) {
        return getFormatter(format, zoneId, DEFAULT_LOCALE);
    }

    static TimestampFormatter getFormatter(Option<String> format, ZoneId zoneId, Locale locale) {
        if (format.isDefined()) {
            return new Iso8601TimestampFormatter(format.get(), zoneId, locale);
        } else {
            return new DefaultTimestampFormatter(zoneId, locale);
        }
    }
}
