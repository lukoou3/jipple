package com.jipple.sql.catalyst.util;

import com.jipple.collection.Option;
import com.jipple.unsafe.types.UTF8String;

import java.time.*;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.jipple.sql.catalyst.util.DateTimeConstants.*;

/**
 * Utility class for date and timestamp conversions.
 * This class provides methods to convert strings to dates and timestamps.
 */
public class JippleDateTimeUtils {

    public static final TimeZone TimeZoneUTC = TimeZone.getTimeZone("UTC");

    // See issue SPARK-35679
    // min second cause overflow in instant to micro
    private static final long MIN_SECONDS = Math.floorDiv(Long.MIN_VALUE, MICROS_PER_SECOND);

    /**
     * Gets a ZoneId from a time zone ID string, with support for legacy formats.
     * 
     * @param timeZoneId the time zone ID string
     * @return the ZoneId
     */
    public static ZoneId getZoneId(String timeZoneId) {
        String formattedZoneId = timeZoneId
                // To support the (+|-)h:mm format because it was supported before Ripple 3.0.
                .replaceFirst("([+\\-])(\\d):", "$10$2:")
                // To support the (+|-)hh:m format because it was supported before Ripple 3.0.
                .replaceFirst("([+\\-])(\\d\\d):(\\d)$", "$1$2:0$3");
        
        return ZoneId.of(formattedZoneId, ZoneId.SHORT_IDS);
    }

    /**
     * Converts the local date to the number of days since 1970-01-01.
     */
    public static int localDateToDays(LocalDate localDate) {
        return Math.toIntExact(localDate.toEpochDay());
    }

    /**
     * Gets the number of microseconds since the epoch of 1970-01-01 00:00:00Z from the given
     * instance of java.time.Instant.
     */
    public static long instantToMicros(Instant instant) {
        long secs = instant.getEpochSecond();
        if (secs == MIN_SECONDS) {
            long us = Math.multiplyExact(secs + 1, MICROS_PER_SECOND);
            return Math.addExact(us, TimeUnit.NANOSECONDS.toMicros(instant.getNano()) - MICROS_PER_SECOND);
        } else {
            long us = Math.multiplyExact(secs, MICROS_PER_SECOND);
            return Math.addExact(us, TimeUnit.NANOSECONDS.toMicros(instant.getNano()));
        }
    }

    /**
     * Converts a LocalDateTime to microseconds since the epoch.
     */
    public static long localDateTimeToMicros(LocalDateTime localDateTime) {
        return instantToMicros(localDateTime.toInstant(ZoneOffset.UTC));
    }

    /**
     * Converts the timestamp `micros` from one timezone to another.
     *
     * Time-zone rules, such as daylight savings, mean that not every local date-time
     * is valid for the `toZone` time zone, thus the local date-time may be adjusted.
     */
    public static long convertTz(long micros, ZoneId fromZone, ZoneId toZone) {
        ZonedDateTime rebasedDateTime = getLocalDateTime(micros, toZone).atZone(fromZone);
        return instantToMicros(rebasedDateTime.toInstant());
    }

    /**
     * Gets the local date-time parts (year, month, day and time) of the instant expressed as the
     * number of microseconds since the epoch at the given time zone ID.
     */
    protected static LocalDateTime getLocalDateTime(long micros, ZoneId zoneId) {
        return microsToInstant(micros).atZone(zoneId).toLocalDateTime();
    }


    /**
     * Obtains an instance of `java.time.LocalDate` from the epoch day count.
     */
    public static LocalDate daysToLocalDate(int days) {
        return LocalDate.ofEpochDay(days);
    }

    /**
     * Converts microseconds since 1970-01-01 00:00:00Z to days since 1970-01-01 at the given zone ID.
     */
    public static int microsToDays(long micros, ZoneId zoneId) {
        return localDateToDays(getLocalDateTime(micros, zoneId).toLocalDate());
    }

    /**
     * Converts days since 1970-01-01 at the given zone ID to microseconds since 1970-01-01 00:00:00Z.
     */
    public static long daysToMicros(int days, ZoneId zoneId) {
        Instant instant = daysToLocalDate(days).atStartOfDay(zoneId).toInstant();
        return instantToMicros(instant);
    }

    /**
     * Obtains an instance of java.time.Instant using microseconds from
     * the epoch of 1970-01-01 00:00:00Z.
     */
    public static Instant microsToInstant(long micros) {
        long secs = Math.floorDiv(micros, MICROS_PER_SECOND);
        // Unfolded Math.floorMod(us, MICROS_PER_SECOND) to reuse the result of
        // the above calculation of `secs` via `floorDiv`.
        long mos = micros - secs * MICROS_PER_SECOND;
        return Instant.ofEpochSecond(secs, mos * NANOS_PER_MICROS);
    }

    /**
     * Trims and parses a given UTF8 date string to a corresponding Int value.
     * The return type is Option in order to distinguish between 0 and null. The following
     * formats are allowed:
     *
     * [+-]yyyy*
     * [+-]yyyy*-[m]m
     * [+-]yyyy*-[m]m-[d]d
     * [+-]yyyy*-[m]m-[d]d 
     * [+-]yyyy*-[m]m-[d]d *
     * [+-]yyyy*-[m]m-[d]dT*
     * 
     * @param s the UTF8String to parse
     * @return Option containing the number of days since 1970-01-01, or None if parsing fails
     */
    public static Option<Integer> stringToDate(UTF8String s) {
        // Helper function to validate digits
        java.util.function.BiFunction<Integer, Integer, Boolean> isValidDigits = (segment, digits) -> {
            // An integer is able to represent a date within [+-]5 million years.
            int maxDigitsYear = 7;
            return (segment == 0 && digits >= 4 && digits <= maxDigitsYear) ||
                    (segment != 0 && digits > 0 && digits <= 2);
        };

        if (s == null || s.trimAll().numBytes() == 0) {
            return Option.none();
        }

        int[] segments = new int[]{1, 1, 1};
        int sign = 1;
        int i = 0;
        int currentSegmentValue = 0;
        int currentSegmentDigits = 0;
        byte[] bytes = s.trimAll().getBytes();
        int j = 0;

        if (bytes.length > 0 && (bytes[j] == '-' || bytes[j] == '+')) {
            sign = (bytes[j] == '-') ? -1 : 1;
            j++;
        }

        while (j < bytes.length && (i < 3 && !(bytes[j] == ' ' || bytes[j] == 'T'))) {
            byte b = bytes[j];
            if (i < 2 && b == '-') {
                if (!isValidDigits.apply(i, currentSegmentDigits)) {
                    return Option.none();
                }
                segments[i] = currentSegmentValue;
                currentSegmentValue = 0;
                currentSegmentDigits = 0;
                i++;
            } else {
                int parsedValue = b - (byte)'0';
                if (parsedValue < 0 || parsedValue > 9) {
                    return Option.none();
                } else {
                    currentSegmentValue = currentSegmentValue * 10 + parsedValue;
                    currentSegmentDigits++;
                }
            }
            j++;
        }

        if (!isValidDigits.apply(i, currentSegmentDigits)) {
            return Option.none();
        }

        if (i < 2 && j < bytes.length) {
            // For the `yyyy` and `yyyy-[m]m` formats, entire input must be consumed.
            return Option.none();
        }

        segments[i] = currentSegmentValue;

        try {
            LocalDate localDate = LocalDate.of(sign * segments[0], segments[1], segments[2]);
            return Option.some(localDateToDays(localDate));
        } catch (Exception e) {
            // NonFatal exceptions are caught and return None
            return Option.none();
        }
    }

    /**
     * Trims and parses a given UTF8 timestamp string to the corresponding timestamp segments,
     * time zone id and whether it is just time without a date.
     * 
     * @param s the UTF8String to parse
     * @return a tuple containing: (segments array, zoneId Option, justTime boolean)
     *         If parsing fails, segments array is empty.
     */
    public static ParseTimestampResult parseTimestampString(UTF8String s) {
        // Helper function to validate digits
        java.util.function.BiFunction<Integer, Integer, Boolean> isValidDigits = (segment, digits) -> {
            // A Long is able to represent a timestamp within [+-]200 thousand years
            int maxDigitsYear = 6;
            // For the nanosecond part, more than 6 digits is allowed, but will be truncated.
            return segment == 6 || (segment == 0 && digits >= 4 && digits <= maxDigitsYear) ||
                    // For the zoneId segment(7), it's could be zero digits when it's a region-based zone ID
                    (segment == 7 && digits <= 2) ||
                    (segment != 0 && segment != 6 && segment != 7 && digits > 0 && digits <= 2);
        };

        if (s == null || s.trimAll().numBytes() == 0) {
            return new ParseTimestampResult(new int[0], Option.none(), false);
        }

        Option<String> tz = Option.none();
        int[] segments = new int[]{1, 1, 1, 0, 0, 0, 0, 0, 0};
        int i = 0;
        int currentSegmentValue = 0;
        int currentSegmentDigits = 0;
        byte[] bytes = s.trimAll().getBytes();
        int j = 0;
        int digitsMilli = 0;
        boolean justTime = false;
        Option<Integer> yearSign = Option.none();

        if (bytes.length > 0 && (bytes[j] == '-' || bytes[j] == '+')) {
            yearSign = Option.some((bytes[j] == '-') ? -1 : 1);
            j++;
        }

        while (j < bytes.length) {
            byte b = bytes[j];
            int parsedValue = b - (byte)'0';
            if (parsedValue < 0 || parsedValue > 9) {
                if (j == 0 && b == 'T') {
                    justTime = true;
                    i += 3;
                } else if (i < 2) {
                    if (b == '-') {
                        if (!isValidDigits.apply(i, currentSegmentDigits)) {
                            return new ParseTimestampResult(new int[0], Option.none(), false);
                        }
                        segments[i] = currentSegmentValue;
                        currentSegmentValue = 0;
                        currentSegmentDigits = 0;
                        i++;
                    } else if (i == 0 && b == ':' && yearSign.isEmpty()) {
                        justTime = true;
                        if (!isValidDigits.apply(3, currentSegmentDigits)) {
                            return new ParseTimestampResult(new int[0], Option.none(), false);
                        }
                        segments[3] = currentSegmentValue;
                        currentSegmentValue = 0;
                        currentSegmentDigits = 0;
                        i = 4;
                    } else {
                        return new ParseTimestampResult(new int[0], Option.none(), false);
                    }
                } else if (i == 2) {
                    if (b == ' ' || b == 'T') {
                        if (!isValidDigits.apply(i, currentSegmentDigits)) {
                            return new ParseTimestampResult(new int[0], Option.none(), false);
                        }
                        segments[i] = currentSegmentValue;
                        currentSegmentValue = 0;
                        currentSegmentDigits = 0;
                        i++;
                    } else {
                        return new ParseTimestampResult(new int[0], Option.none(), false);
                    }
                } else if (i == 3 || i == 4) {
                    if (b == ':') {
                        if (!isValidDigits.apply(i, currentSegmentDigits)) {
                            return new ParseTimestampResult(new int[0], Option.none(), false);
                        }
                        segments[i] = currentSegmentValue;
                        currentSegmentValue = 0;
                        currentSegmentDigits = 0;
                        i++;
                    } else {
                        return new ParseTimestampResult(new int[0], Option.none(), false);
                    }
                } else if (i == 5 || i == 6) {
                    if (b == '.' && i == 5) {
                        if (!isValidDigits.apply(i, currentSegmentDigits)) {
                            return new ParseTimestampResult(new int[0], Option.none(), false);
                        }
                        segments[i] = currentSegmentValue;
                        currentSegmentValue = 0;
                        currentSegmentDigits = 0;
                        i++;
                    } else {
                        if (!isValidDigits.apply(i, currentSegmentDigits)) {
                            return new ParseTimestampResult(new int[0], Option.none(), false);
                        }
                        segments[i] = currentSegmentValue;
                        currentSegmentValue = 0;
                        currentSegmentDigits = 0;
                        i++;
                        tz = Option.some(new String(bytes, j, bytes.length - j));
                        j = bytes.length - 1;
                    }
                    if (i == 6 && b != '.') {
                        i++;
                    }
                } else {
                    if (i < segments.length && (b == ':' || b == ' ')) {
                        if (!isValidDigits.apply(i, currentSegmentDigits)) {
                            return new ParseTimestampResult(new int[0], Option.none(), false);
                        }
                        segments[i] = currentSegmentValue;
                        currentSegmentValue = 0;
                        currentSegmentDigits = 0;
                        i++;
                    } else {
                        return new ParseTimestampResult(new int[0], Option.none(), false);
                    }
                }
            } else {
                if (i == 6) {
                    digitsMilli++;
                }
                // We will truncate the nanosecond part if there are more than 6 digits, which results
                // in loss of precision
                if (i != 6 || currentSegmentDigits < 6) {
                    currentSegmentValue = currentSegmentValue * 10 + parsedValue;
                }
                currentSegmentDigits++;
            }
            j++;
        }

        if (!isValidDigits.apply(i, currentSegmentDigits)) {
            return new ParseTimestampResult(new int[0], Option.none(), false);
        }

        segments[i] = currentSegmentValue;

        while (digitsMilli < 6) {
            segments[6] *= 10;
            digitsMilli++;
        }

        // This step also validates time zone part
        Option<ZoneId> zoneId = tz.map(zoneName -> getZoneId(zoneName.trim()));
        segments[0] *= yearSign.getOrElse(1);
        return new ParseTimestampResult(segments, zoneId, justTime);
    }

    /**
     * Result class for parseTimestampString method.
     */
    public static class ParseTimestampResult {
        public final int[] segments;
        public final Option<ZoneId> zoneId;
        public final boolean justTime;

        public ParseTimestampResult(int[] segments, Option<ZoneId> zoneId, boolean justTime) {
            this.segments = segments;
            this.zoneId = zoneId;
            this.justTime = justTime;
        }
    }

    /**
     * Trims and parses a given UTF8 timestamp string to the corresponding a corresponding Long
     * value. The return type is Option in order to distinguish between 0L and null.
     * Please refer to parseTimestampString for the allowed formats.
     * 
     * @param s the UTF8String to parse
     * @param timeZoneId the default time zone ID
     * @return Option containing the number of microseconds since epoch, or None if parsing fails
     */
    public static Option<Long> stringToTimestamp(UTF8String s, ZoneId timeZoneId) {
        try {
            ParseTimestampResult result = parseTimestampString(s);
            if (result.segments.length == 0) {
                return Option.none();
            }
            ZoneId zoneId = result.zoneId.getOrElse(timeZoneId);
            long nanoseconds = TimeUnit.MICROSECONDS.toNanos(result.segments[6]);
            LocalTime localTime = LocalTime.of(result.segments[3], result.segments[4], 
                    result.segments[5], (int)nanoseconds);
            LocalDate localDate;
            if (result.justTime) {
                localDate = LocalDate.now(zoneId);
            } else {
                localDate = LocalDate.of(result.segments[0], result.segments[1], result.segments[2]);
            }
            LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
            Instant instant = Instant.from(zonedDateTime);
            return Option.some(instantToMicros(instant));
        } catch (Exception e) {
            // NonFatal exceptions are caught and return None
            return Option.none();
        }
    }

}
