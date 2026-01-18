package com.jipple.sql.catalyst.util;

import com.jipple.collection.Option;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class TimestampFormatterTest {
    private static final ZoneId UTC = ZoneOffset.UTC;

    @Test
    void iso8601ParsesAndFormatsDefaultPattern() {
        // 使用 UTC 时区，验证默认 pattern 的解析与格式化互相一致
        Iso8601TimestampFormatter formatter =
                new Iso8601TimestampFormatter(TimestampFormatter.defaultPattern(), UTC, TimestampFormatter.DEFAULT_LOCALE);
        String input = "2024-02-03 04:05:06";
        long micros = formatter.parse(input);
        long expected = toMicros(LocalDateTime.of(2024, 2, 3, 4, 5, 6).toInstant(ZoneOffset.UTC));
        assertEquals(expected, micros);
        assertEquals(input, formatter.format(micros));
    }

    @Test
    void parseOptionalReturnsNoneOnInvalidInput() {
        // parseOptional 遇到非法输入返回 Option.none()
        Iso8601TimestampFormatter formatter =
                new Iso8601TimestampFormatter(TimestampFormatter.defaultPattern(), UTC, TimestampFormatter.DEFAULT_LOCALE);
        Option<Long> valid = formatter.parseOptional("2024-02-03 04:05:06");
        Option<Long> invalid = formatter.parseOptional("not a timestamp");
        assertTrue(valid.isDefined());
        assertTrue(invalid.isEmpty());
    }

    @Test
    void parseWithoutTimeZoneRejectsZoneWhenNotAllowed() {
        // allowTimeZone=false 时，解析带时区的字符串应抛异常
        Iso8601TimestampFormatter formatter =
                new Iso8601TimestampFormatter("yyyy-MM-dd HH:mm:ssXXX", UTC, TimestampFormatter.DEFAULT_LOCALE);
        assertThrows(DateTimeParseException.class,
                () -> formatter.parseWithoutTimeZone("2024-02-03 04:05:06+02:00", false));
        // allowTimeZone=true 时，时区信息会被忽略，按本地时间解析
        long micros = formatter.parseWithoutTimeZone("2024-02-03 04:05:06+02:00", true);
        long expected = toMicros(LocalDateTime.of(2024, 2, 3, 4, 5, 6).toInstant(ZoneOffset.UTC));
        assertEquals(expected, micros);
    }

    @Test
    void fractionFormatterOmitsTrailingZeros() {
        // 小数秒末尾 0 会被省略
        FractionTimestampFormatter formatter = new FractionTimestampFormatter(UTC);
        Instant instant = Instant.ofEpochSecond(0, 123_400_000);
        assertEquals("1970-01-01 00:00:00.1234", formatter.format(instant));
        Instant zero = Instant.ofEpochSecond(0, 0);
        assertEquals("1970-01-01 00:00:00", formatter.format(zero));
    }

    @Test
    void fractionFormatterParsesFractionalInput() {
        // 解析带小数秒的输入，结果为微秒
        FractionTimestampFormatter formatter = new FractionTimestampFormatter(UTC);
        long micros = formatter.parse("1970-01-01 00:00:00.1234");
        assertEquals(123_400L, micros);
    }

    @Test
    void iso8601ParsesAndFormatsWithChinaTimeZone() {
        // 使用中国时区（Asia/Shanghai），验证解析与格式化的一致性
        ZoneId chinaZone = ZoneId.of("Asia/Shanghai");
        Iso8601TimestampFormatter formatter =
                new Iso8601TimestampFormatter(TimestampFormatter.defaultPattern(), chinaZone, TimestampFormatter.DEFAULT_LOCALE);
        String input = "2024-02-03 04:05:06";
        long micros = formatter.parse(input);
        long expected = toMicros(LocalDateTime.of(2024, 2, 3, 4, 5, 6).atZone(chinaZone).toInstant());
        assertEquals(expected, micros);
        assertEquals(input, formatter.format(micros));
    }

    private static long toMicros(Instant instant) {
        // 由 Instant 计算微秒时间戳
        return instant.getEpochSecond() * 1_000_000L + instant.getNano() / 1_000L;
    }
}
