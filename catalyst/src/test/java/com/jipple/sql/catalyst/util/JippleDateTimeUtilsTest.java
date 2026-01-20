package com.jipple.sql.catalyst.util;

import com.jipple.collection.Option;
import com.jipple.unsafe.types.UTF8String;
import org.junit.jupiter.api.Test;

import java.time.*;

import static com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JippleDateTimeUtils.
 */
public class JippleDateTimeUtilsTest {

    private static final ZoneId UTC = ZoneOffset.UTC;
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    @Test
    public void testGetZoneId() {
        // Test standard time zone
        assertEquals(ZoneId.of("UTC"), JippleDateTimeUtils.getZoneId("UTC"));
        assertEquals(ZoneId.of("Asia/Shanghai"), JippleDateTimeUtils.getZoneId("Asia/Shanghai"));
        assertEquals(ZoneId.of("America/New_York"), JippleDateTimeUtils.getZoneId("America/New_York"));

        // Test legacy format: (+|-)h:mm
        assertEquals(ZoneId.of("+01:00"), JippleDateTimeUtils.getZoneId("+1:00"));
        assertEquals(ZoneId.of("-05:00"), JippleDateTimeUtils.getZoneId("-5:00"));

        // Test legacy format: (+|-)hh:m
        assertEquals(ZoneId.of("+10:00"), JippleDateTimeUtils.getZoneId("+10:0"));
        assertEquals(ZoneId.of("-08:00"), JippleDateTimeUtils.getZoneId("-08:0"));

        // Test standard offset format
        assertEquals(ZoneId.of("+08:00"), JippleDateTimeUtils.getZoneId("+08:00"));
        assertEquals(ZoneId.of("-05:00"), JippleDateTimeUtils.getZoneId("-05:00"));
    }

    @Test
    public void testLocalDateToDays() {
        // Test epoch date (1970-01-01)
        LocalDate epoch = LocalDate.of(1970, 1, 1);
        assertEquals(0, JippleDateTimeUtils.localDateToDays(epoch));

        // Test a date after epoch
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        long expectedDays1 = date1.toEpochDay();
        assertEquals(expectedDays1, JippleDateTimeUtils.localDateToDays(date1));

        // Test a date before epoch
        LocalDate date2 = LocalDate.of(1960, 1, 1);
        long expectedDays2 = date2.toEpochDay();
        assertEquals(expectedDays2, JippleDateTimeUtils.localDateToDays(date2));
    }

    @Test
    public void testInstantToMicros() {
        // Test epoch instant
        Instant epoch = Instant.ofEpochSecond(0, 0);
        assertEquals(0L, JippleDateTimeUtils.instantToMicros(epoch));

        // Test instant with seconds and nanoseconds
        Instant instant1 = Instant.ofEpochSecond(1234567890L, 123456789);
        long expected1 = 1234567890L * MICROS_PER_SECOND + 123456789 / 1000;
        assertEquals(expected1, JippleDateTimeUtils.instantToMicros(instant1));

        // Test instant with only seconds
        Instant instant2 = Instant.ofEpochSecond(1609459200L); // 2021-01-01 00:00:00 UTC
        long expected2 = 1609459200L * MICROS_PER_SECOND;
        assertEquals(expected2, JippleDateTimeUtils.instantToMicros(instant2));
    }

    @Test
    public void testLocalDateTimeToMicros() {
        // Test epoch local date time
        LocalDateTime epoch = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        long expected = epoch.toInstant(ZoneOffset.UTC).getEpochSecond() * MICROS_PER_SECOND;
        assertEquals(expected, JippleDateTimeUtils.localDateTimeToMicros(epoch));

        // Test a specific date time
        LocalDateTime dateTime = LocalDateTime.of(2024, 2, 3, 14, 30, 45, 123456789);
        long expectedMicros = dateTime.toInstant(ZoneOffset.UTC).getEpochSecond() * MICROS_PER_SECOND
                + dateTime.toInstant(ZoneOffset.UTC).getNano() / 1000;
        assertEquals(expectedMicros, JippleDateTimeUtils.localDateTimeToMicros(dateTime));
    }

    @Test
    public void testMicrosToInstant() {
        // Test epoch micros
        Instant epoch = JippleDateTimeUtils.microsToInstant(0L);
        assertEquals(Instant.ofEpochSecond(0, 0), epoch);

        // Test round-trip conversion
        Instant original = Instant.ofEpochSecond(1234567890L, 123456789);
        long micros = JippleDateTimeUtils.instantToMicros(original);
        Instant converted = JippleDateTimeUtils.microsToInstant(micros);
        // Note: nanoseconds precision may be lost, so we compare at microsecond level
        assertEquals(original.getEpochSecond(), converted.getEpochSecond());
        assertEquals(original.getNano() / 1000 * 1000, converted.getNano() / 1000 * 1000);
    }

    @Test
    public void testStringToDate() {
        // Test valid date formats
        Option<Integer> result1 = JippleDateTimeUtils.stringToDate(UTF8String.fromString("2024-01-15"));
        assertTrue(result1.isDefined());
        LocalDate date1 = LocalDate.of(2024, 1, 15);
        assertEquals(date1.toEpochDay(), result1.get().longValue());

        Option<Integer> result2 = JippleDateTimeUtils.stringToDate(UTF8String.fromString("2024-1-5"));
        assertTrue(result2.isDefined());
        LocalDate date2 = LocalDate.of(2024, 1, 5);
        assertEquals(date2.toEpochDay(), result2.get().longValue());

        Option<Integer> result3 = JippleDateTimeUtils.stringToDate(UTF8String.fromString("2024-12-31"));
        assertTrue(result3.isDefined());
        LocalDate date3 = LocalDate.of(2024, 12, 31);
        assertEquals(date3.toEpochDay(), result3.get().longValue());

        // Test with sign
        Option<Integer> result4 = JippleDateTimeUtils.stringToDate(UTF8String.fromString("-2024-01-15"));
        assertTrue(result4.isDefined());
        LocalDate date4 = LocalDate.of(-2024, 1, 15);
        assertEquals(date4.toEpochDay(), result4.get().longValue());

        // Test invalid formats
        assertTrue(JippleDateTimeUtils.stringToDate(UTF8String.fromString("invalid")).isEmpty());
        assertTrue(JippleDateTimeUtils.stringToDate(UTF8String.fromString("2024-13-01")).isEmpty());
        assertTrue(JippleDateTimeUtils.stringToDate(UTF8String.fromString("2024-01-32")).isEmpty());
        assertTrue(JippleDateTimeUtils.stringToDate(UTF8String.fromString("")).isEmpty());
        assertTrue(JippleDateTimeUtils.stringToDate(null).isEmpty());

        // Test with spaces (should be trimmed)
        Option<Integer> result5 = JippleDateTimeUtils.stringToDate(UTF8String.fromString("  2024-01-15  "));
        assertTrue(result5.isDefined());
        assertEquals(date1.toEpochDay(), result5.get().longValue());
    }

    @Test
    public void testParseTimestampString() {
        // Test valid timestamp with date and time
        JippleDateTimeUtils.ParseTimestampResult result1 = 
                JippleDateTimeUtils.parseTimestampString(UTF8String.fromString("2024-01-15 14:30:45"));
        assertTrue(result1.segments.length > 0);
        assertEquals(2024, result1.segments[0]);
        assertEquals(1, result1.segments[1]);
        assertEquals(15, result1.segments[2]);
        assertEquals(14, result1.segments[3]);
        assertEquals(30, result1.segments[4]);
        assertEquals(45, result1.segments[5]);
        assertFalse(result1.justTime);

        // Test timestamp with microseconds
        JippleDateTimeUtils.ParseTimestampResult result2 = 
                JippleDateTimeUtils.parseTimestampString(UTF8String.fromString("2024-01-15 14:30:45.123456"));
        assertTrue(result2.segments.length > 0);
        assertEquals(123456, result2.segments[6]);

        // Test timestamp with timezone
        JippleDateTimeUtils.ParseTimestampResult result3 = 
                JippleDateTimeUtils.parseTimestampString(UTF8String.fromString("2024-01-15 14:30:45+08:00"));
        assertTrue(result3.segments.length > 0);
        assertTrue(result3.zoneId.isDefined());

        // Test just time (without date)
        JippleDateTimeUtils.ParseTimestampResult result4 = 
                JippleDateTimeUtils.parseTimestampString(UTF8String.fromString("14:30:45"));
        assertTrue(result4.segments.length > 0);
        assertTrue(result4.justTime);
        assertEquals(14, result4.segments[3]);
        assertEquals(30, result4.segments[4]);
        assertEquals(45, result4.segments[5]);

        // Test invalid formats
        JippleDateTimeUtils.ParseTimestampResult result5 = 
                JippleDateTimeUtils.parseTimestampString(UTF8String.fromString("invalid"));
        assertEquals(0, result5.segments.length);

        JippleDateTimeUtils.ParseTimestampResult result6 = 
                JippleDateTimeUtils.parseTimestampString(UTF8String.fromString(""));
        assertEquals(0, result6.segments.length);

        JippleDateTimeUtils.ParseTimestampResult result7 = 
                JippleDateTimeUtils.parseTimestampString(null);
        assertEquals(0, result7.segments.length);
    }

    @Test
    public void testStringToTimestamp() {
        // Test valid timestamp
        Option<Long> result1 = JippleDateTimeUtils.stringToTimestamp(
                UTF8String.fromString("2024-01-15 14:30:45"), UTC);
        assertTrue(result1.isDefined());
        LocalDateTime expected1 = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        long expectedMicros1 = JippleDateTimeUtils.localDateTimeToMicros(expected1);
        assertEquals(expectedMicros1, result1.get().longValue());

        // Test timestamp with microseconds
        Option<Long> result2 = JippleDateTimeUtils.stringToTimestamp(
                UTF8String.fromString("2024-01-15 14:30:45.123456"), UTC);
        assertTrue(result2.isDefined());
        LocalDateTime expected2 = LocalDateTime.of(2024, 1, 15, 14, 30, 45, 123456000);
        long expectedMicros2 = JippleDateTimeUtils.localDateTimeToMicros(expected2);
        assertEquals(expectedMicros2, result2.get().longValue());

        // Test timestamp with timezone
        Option<Long> result3 = JippleDateTimeUtils.stringToTimestamp(
                UTF8String.fromString("2024-01-15 14:30:45+08:00"), UTC);
        assertTrue(result3.isDefined());
        // The result should be converted to UTC
        ZonedDateTime zoned = ZonedDateTime.of(2024, 1, 15, 14, 30, 45, 0, SHANGHAI);
        long expectedMicros3 = JippleDateTimeUtils.instantToMicros(zoned.toInstant());
        assertEquals(expectedMicros3, result3.get().longValue());

        // Test just time (uses current date)
        Option<Long> result4 = JippleDateTimeUtils.stringToTimestamp(
                UTF8String.fromString("14:30:45"), UTC);
        assertTrue(result4.isDefined());
        // Verify it's a valid timestamp (just check it's defined)

        // Test invalid formats
        assertTrue(JippleDateTimeUtils.stringToTimestamp(
                UTF8String.fromString("invalid"), UTC).isEmpty());
        assertTrue(JippleDateTimeUtils.stringToTimestamp(
                UTF8String.fromString(""), UTC).isEmpty());
        assertTrue(JippleDateTimeUtils.stringToTimestamp(
                null, UTC).isEmpty());
    }

    @Test
    public void testRoundTripConversions() {
        // Test instant round-trip
        Instant originalInstant = Instant.ofEpochSecond(1234567890L, 123456789);
        long micros = JippleDateTimeUtils.instantToMicros(originalInstant);
        Instant convertedInstant = JippleDateTimeUtils.microsToInstant(micros);
        assertEquals(originalInstant.getEpochSecond(), convertedInstant.getEpochSecond());
        assertEquals(originalInstant.getNano() / 1000 * 1000, convertedInstant.getNano() / 1000 * 1000);

        // Test LocalDateTime round-trip
        LocalDateTime originalDateTime = LocalDateTime.of(2024, 2, 3, 14, 30, 45, 123456789);
        long micros2 = JippleDateTimeUtils.localDateTimeToMicros(originalDateTime);
        Instant instant2 = JippleDateTimeUtils.microsToInstant(micros2);
        LocalDateTime convertedDateTime = LocalDateTime.ofInstant(instant2, UTC);
        assertEquals(originalDateTime.getYear(), convertedDateTime.getYear());
        assertEquals(originalDateTime.getMonth(), convertedDateTime.getMonth());
        assertEquals(originalDateTime.getDayOfMonth(), convertedDateTime.getDayOfMonth());
        assertEquals(originalDateTime.getHour(), convertedDateTime.getHour());
        assertEquals(originalDateTime.getMinute(), convertedDateTime.getMinute());
        assertEquals(originalDateTime.getSecond(), convertedDateTime.getSecond());
    }

    @Test
    public void testEdgeCases() {
        // Test minimum date supported by DateType: 0001-01-01
        LocalDate minDate = LocalDate.of(1, 1, 1);
        int days = JippleDateTimeUtils.localDateToDays(minDate);
        assertEquals(minDate.toEpochDay(), days);

        // Test maximum date supported by DateType: 9999-12-31
        LocalDate maxDate = LocalDate.of(9999, 12, 31);
        int days2 = JippleDateTimeUtils.localDateToDays(maxDate);
        assertEquals(maxDate.toEpochDay(), days2);

        // Test string parsing for minimum date
        Option<Integer> resultMin = JippleDateTimeUtils.stringToDate(UTF8String.fromString("0001-01-01"));
        assertTrue(resultMin.isDefined());
        assertEquals(minDate.toEpochDay(), resultMin.get().longValue());

        // Test string parsing for maximum date
        Option<Integer> resultMax = JippleDateTimeUtils.stringToDate(UTF8String.fromString("9999-12-31"));
        assertTrue(resultMax.isDefined());
        assertEquals(maxDate.toEpochDay(), resultMax.get().longValue());

        // Test date with spaces and T separator
        Option<Integer> result1 = JippleDateTimeUtils.stringToDate(UTF8String.fromString("2024-01-15T"));
        assertTrue(result1.isDefined());
        LocalDate date1 = LocalDate.of(2024, 1, 15);
        assertEquals(date1.toEpochDay(), result1.get().longValue());

        Option<Integer> result2 = JippleDateTimeUtils.stringToDate(UTF8String.fromString("2024-01-15 "));
        assertTrue(result2.isDefined());
        assertEquals(date1.toEpochDay(), result2.get().longValue());
    }

    @Test
    public void testTimestampWithDifferentFormats() {
        // Test ISO format with T separator
        Option<Long> result1 = JippleDateTimeUtils.stringToTimestamp(
                UTF8String.fromString("2024-01-15T14:30:45"), UTC);
        assertTrue(result1.isDefined());

        // Test timestamp with milliseconds (less than 6 digits)
        Option<Long> result2 = JippleDateTimeUtils.stringToTimestamp(
                UTF8String.fromString("2024-01-15 14:30:45.123"), UTC);
        assertTrue(result2.isDefined());
        // Should be padded to microseconds: 123000

        // Test timestamp with more than 6 digits (should be truncated)
        Option<Long> result3 = JippleDateTimeUtils.stringToTimestamp(
                UTF8String.fromString("2024-01-15 14:30:45.123456789"), UTC);
        assertTrue(result3.isDefined());
        // Should truncate to 6 digits: 123456
    }
}

