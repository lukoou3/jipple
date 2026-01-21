package com.jipple.sql.catalyst.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Utility class for date and timestamp conversions between Java types and Catalyst internal types.
 * This class provides methods to convert java.sql.Date/Timestamp to/from Catalyst internal representations.
 */
public class DateTimeUtils {

    /**
     * Converts a java.sql.Date to the number of days since 1970-01-01.
     */
    public static int fromJavaDate(Date date) {
        return JippleDateTimeUtils.localDateToDays(date.toLocalDate());
    }

    /**
     * Converts the number of days since 1970-01-01 to a java.sql.Date.
     */
    public static Date toJavaDate(int days) {
        LocalDate localDate = JippleDateTimeUtils.daysToLocalDate(days);
        return Date.valueOf(localDate);
    }

    /**
     * Converts a java.sql.Timestamp to the number of microseconds since 1970-01-01 00:00:00Z.
     */
    public static long fromJavaTimestamp(Timestamp timestamp) {
        return JippleDateTimeUtils.instantToMicros(timestamp.toInstant());
    }

    /**
     * Converts the number of microseconds since 1970-01-01 00:00:00Z to a java.sql.Timestamp.
     */
    public static Timestamp toJavaTimestamp(long micros) {
        Instant instant = JippleDateTimeUtils.microsToInstant(micros);
        return Timestamp.from(instant);
    }

    /**
     * Converts a LocalDate to the number of days since 1970-01-01.
     */
    public static int localDateToDays(LocalDate localDate) {
        return JippleDateTimeUtils.localDateToDays(localDate);
    }

    /**
     * Converts the number of days since 1970-01-01 to a LocalDate.
     */
    public static LocalDate daysToLocalDate(int days) {
        return JippleDateTimeUtils.daysToLocalDate(days);
    }

    /**
     * Converts an Instant to the number of microseconds since 1970-01-01 00:00:00Z.
     */
    public static long instantToMicros(Instant instant) {
        return JippleDateTimeUtils.instantToMicros(instant);
    }

    /**
     * Converts the number of microseconds since 1970-01-01 00:00:00Z to an Instant.
     */
    public static Instant microsToInstant(long micros) {
        return JippleDateTimeUtils.microsToInstant(micros);
    }

    /**
     * Converts a LocalDateTime to microseconds since the epoch.
     */
    public static long localDateTimeToMicros(LocalDateTime localDateTime) {
        return JippleDateTimeUtils.localDateTimeToMicros(localDateTime);
    }

    /**
     * Converts microseconds since the epoch to a LocalDateTime.
     */
    public static LocalDateTime microsToLocalDateTime(long micros) {
        return JippleDateTimeUtils.microsToInstant(micros).atZone(java.time.ZoneOffset.UTC).toLocalDateTime();
    }
}

