package com.jipple.sql.catalyst.util;

import com.jipple.collection.Option;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.unsafe.types.UTF8String;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.jipple.sql.catalyst.util.DateTimeConstants.*;

/**
 * Utility class for date and timestamp conversions.
 * This class provides methods to convert strings to dates and timestamps.
 */
public class JippleDateTimeUtils {

    public static final TimeZone TimeZoneUTC = TimeZone.getTimeZone("UTC");

    // The constants are visible for testing purpose only.
    public static final int TRUNC_INVALID = -1;
    // The levels from TRUNC_TO_MICROSECOND to TRUNC_TO_DAY are used in truncations
    // of TIMESTAMP values only.
    public static final int TRUNC_TO_MICROSECOND = 0;
    public static final int MIN_LEVEL_OF_TIMESTAMP_TRUNC = TRUNC_TO_MICROSECOND;
    public static final int TRUNC_TO_MILLISECOND = 1;
    public static final int TRUNC_TO_SECOND = 2;
    public static final int TRUNC_TO_MINUTE = 3;
    public static final int TRUNC_TO_HOUR = 4;
    public static final int TRUNC_TO_DAY = 5;
    // The levels from TRUNC_TO_WEEK to TRUNC_TO_YEAR are used in truncations
    // of DATE and TIMESTAMP values.
    public static final int TRUNC_TO_WEEK = 6;
    public static final int MIN_LEVEL_OF_DATE_TRUNC = TRUNC_TO_WEEK;
    public static final int TRUNC_TO_MONTH = 7;
    public static final int TRUNC_TO_QUARTER = 8;
    public static final int TRUNC_TO_YEAR = 9;

    // Thursday = 0 since 1970/Jan/01 => Thursday
    private static final int SUNDAY = 3;
    private static final int MONDAY = 4;
    private static final int TUESDAY = 5;
    private static final int WEDNESDAY = 6;
    private static final int THURSDAY = 0;
    private static final int FRIDAY = 1;
    private static final int SATURDAY = 2;

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
     * Returns the trunc date from original date and trunc level.
     * Trunc level should be generated using `parseTruncLevel()`, should be between 6 and 9.
     */
    public static int truncDate(int days, int level) {
        switch (level) {
            case TRUNC_TO_WEEK:
                return getNextDateForDayOfWeek(days - 7, MONDAY);
            case TRUNC_TO_MONTH:
                return days - getDayOfMonth(days) + 1;
            case TRUNC_TO_QUARTER:
                return localDateToDays(daysToLocalDate(days).with(IsoFields.DAY_OF_QUARTER, 1L));
            case TRUNC_TO_YEAR:
                return days - getDayInYear(days) + 1;
            default:
                throw QueryExecutionErrors.unreachableError(": Invalid trunc level: " + level);
        }
    }

    private static long truncToUnit(long micros, ZoneId zoneId, ChronoUnit unit) {
        ZonedDateTime truncated = microsToInstant(micros).atZone(zoneId).truncatedTo(unit);
        return instantToMicros(truncated.toInstant());
    }

    /**
     * Returns the trunc date time from original date time and trunc level.
     * Trunc level should be generated using `parseTruncLevel()`, should be between 0 and 9.
     */
    public static long truncTimestamp(long micros, int level, ZoneId zoneId) {
        // Time zone offsets have a maximum precision of seconds (see `java.time.ZoneOffset`). Hence
        // truncation to microsecond, millisecond, and second can be done
        // without using time zone information. This results in a performance improvement.
        switch (level) {
            case TRUNC_TO_MICROSECOND:
                return micros;
            case TRUNC_TO_MILLISECOND:
                return micros - Math.floorMod(micros, MICROS_PER_MILLIS);
            case TRUNC_TO_SECOND:
                return micros - Math.floorMod(micros, MICROS_PER_SECOND);
            case TRUNC_TO_MINUTE:
                return truncToUnit(micros, zoneId, ChronoUnit.MINUTES);
            case TRUNC_TO_HOUR:
                return truncToUnit(micros, zoneId, ChronoUnit.HOURS);
            case TRUNC_TO_DAY:
                return truncToUnit(micros, zoneId, ChronoUnit.DAYS);
            default:
                int dDays = microsToDays(micros, zoneId);
                return daysToMicros(truncDate(dDays, level), zoneId);
        }
    }

    /**
     * Returns the truncate level, could be from TRUNC_TO_MICROSECOND to TRUNC_TO_YEAR,
     * or TRUNC_INVALID, TRUNC_INVALID means unsupported truncate level.
     */
    public static int parseTruncLevel(UTF8String format) {
        if (format == null) {
            return TRUNC_INVALID;
        }
        switch (format.toString().toUpperCase(Locale.ROOT)) {
            case "MICROSECOND":
                return TRUNC_TO_MICROSECOND;
            case "MILLISECOND":
                return TRUNC_TO_MILLISECOND;
            case "SECOND":
                return TRUNC_TO_SECOND;
            case "MINUTE":
                return TRUNC_TO_MINUTE;
            case "HOUR":
                return TRUNC_TO_HOUR;
            case "DAY":
            case "DD":
                return TRUNC_TO_DAY;
            case "WEEK":
                return TRUNC_TO_WEEK;
            case "MON":
            case "MONTH":
            case "MM":
                return TRUNC_TO_MONTH;
            case "QUARTER":
                return TRUNC_TO_QUARTER;
            case "YEAR":
            case "YYYY":
            case "YY":
                return TRUNC_TO_YEAR;
            default:
                return TRUNC_INVALID;
        }
    }

    /**
     * Returns day of week from String. Starting from Thursday, marked as 0.
     * (Because 1970-01-01 is Thursday).
     *
     * @throws IllegalArgumentException if the input is not a valid day of week.
     */
    public static int getDayOfWeekFromString(UTF8String string) {
        String dowString = string.toString().toUpperCase(Locale.ROOT);
        switch (dowString) {
            case "SU":
            case "SUN":
            case "SUNDAY":
                return SUNDAY;
            case "MO":
            case "MON":
            case "MONDAY":
                return MONDAY;
            case "TU":
            case "TUE":
            case "TUESDAY":
                return TUESDAY;
            case "WE":
            case "WED":
            case "WEDNESDAY":
                return WEDNESDAY;
            case "TH":
            case "THU":
            case "THURSDAY":
                return THURSDAY;
            case "FR":
            case "FRI":
            case "FRIDAY":
                return FRIDAY;
            case "SA":
            case "SAT":
            case "SATURDAY":
                return SATURDAY;
            default:
                throw new IllegalArgumentException("Illegal input for day of week: " + string);
        }
    }

    /**
     * Returns the first date which is later than startDate and is of the given dayOfWeek.
     * dayOfWeek is an integer ranges in [0, 6], and 0 is Thu, 1 is Fri, etc,.
     */
    public static int getNextDateForDayOfWeek(int startDay, int dayOfWeek) {
        return startDay + 1 + Math.floorMod(dayOfWeek - 1 - startDay, 7);
    }

    private static int getDayOfMonth(int days) {
        return daysToLocalDate(days).getDayOfMonth();
    }

    private static int getDayInYear(int days) {
        return daysToLocalDate(days).getDayOfYear();
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
