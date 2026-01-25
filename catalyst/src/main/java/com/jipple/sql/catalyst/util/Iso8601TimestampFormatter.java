package com.jipple.sql.catalyst.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Locale;

import static com.jipple.sql.catalyst.util.DateTimeConstants.MICROS_PER_SECOND;

public class Iso8601TimestampFormatter implements TimestampFormatter {
    private static final long serialVersionUID = 1L;
    private final String pattern;
    private final ZoneId zoneId;
    private final Locale locale;
    private transient DateTimeFormatter formatter;

    public Iso8601TimestampFormatter(
            String pattern,
            ZoneId zoneId,
            Locale locale) {
        this.pattern = pattern;
        this.zoneId = zoneId;
        this.locale = locale;
        this.formatter = DateTimeFormatter.ofPattern(pattern, locale);
    }

    public Iso8601TimestampFormatter(
            String pattern,
            ZoneId zoneId) {
        this(pattern, zoneId, DEFAULT_LOCALE);
    }

    @Override
    public long parse(String s) throws DateTimeParseException {
        TemporalAccessor parsed = formatter().parse(s);
        ZoneId parsedZone = parsed.query(TemporalQueries.zone());
        if (parsedZone != null) {
            return instantToMicros(ZonedDateTime.from(parsed).toInstant());
        }
        LocalDate date = LocalDate.from(parsed);
        LocalTime time = LocalTime.from(parsed);
        ZonedDateTime zoned = ZonedDateTime.of(date, time, zoneId);
        return instantToMicros(zoned.toInstant());
    }

    @Override
    public long parseWithoutTimeZone(String s, boolean allowTimeZone) throws DateTimeParseException {
        TemporalAccessor parsed = formatter().parse(s);
        if (!allowTimeZone && parsed.query(TemporalQueries.zone()) != null) {
            throw new DateTimeParseException("Time zone is not allowed", s, 0);
        }
        LocalDate date = LocalDate.from(parsed);
        LocalTime time = LocalTime.from(parsed);
        return localDateTimeToMicros(LocalDateTime.of(date, time));
    }

    @Override
    public String format(long us) {
        return format(microsToInstant(us));
    }

    @Override
    public String format(Timestamp ts) {
        return format(ts.toInstant());
    }

    @Override
    public String format(Instant instant) {
        return formatter().withZone(zoneId).format(instant);
    }

    @Override
    public String format(LocalDateTime localDateTime) {
        return localDateTime.format(formatter());
    }

    @Override
    public void validatePatternString(boolean checkLegacy) {
        DateTimeFormatter.ofPattern(pattern, locale);
    }

    protected ZoneId zoneId() {
        return zoneId;
    }

    protected String pattern() {
        return pattern;
    }

    protected DateTimeFormatter formatter() {
        return formatter;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.formatter = DateTimeFormatter.ofPattern(pattern, locale);
    }

    private static long instantToMicros(Instant instant) {
        long seconds = instant.getEpochSecond();
        long micros = instant.getNano() / 1_000L;
        return Math.addExact(seconds * MICROS_PER_SECOND, micros);
    }

    private static Instant microsToInstant(long micros) {
        long secs = Math.floorDiv(micros, MICROS_PER_SECOND);
        long mos = micros - secs * MICROS_PER_SECOND;
        return Instant.ofEpochSecond(secs, mos * 1_000L);
    }

    private static long localDateTimeToMicros(LocalDateTime localDateTime) {
        return instantToMicros(localDateTime.toInstant(ZoneOffset.UTC));
    }
}
