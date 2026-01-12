package com.jipple.sql.types;


import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DecimalTest {

    /**
     * Static field for accessing the private decimalVal field via reflection.
     * Initialized once for performance.
     */
    private static final Field DECIMAL_VAL_FIELD;

    static {
        try {
            DECIMAL_VAL_FIELD = Decimal.class.getDeclaredField("decimalVal");
            DECIMAL_VAL_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access decimalVal field", e);
        }
    }

    /** Check that a Decimal has the given string representation, precision and scale */
    private void checkDecimal(Decimal d, String string, int precision, int scale) {
        assertEquals(d.toString(), string);
        assertEquals(d.precision(), precision);
        assertEquals(d.scale(), scale);
    }

    @Test
    public void testCreateDecimals() {
        checkDecimal(new Decimal(), "0", 1, 0);
        checkDecimal(new Decimal(new BigDecimal("0.09")), "0.09", 2, 2);
        checkDecimal(new Decimal(new BigDecimal("0.9")), "0.9", 1, 1);
        checkDecimal(new Decimal(new BigDecimal("0.90")), "0.90", 2, 2);
        checkDecimal(new Decimal(new BigDecimal("0.0")), "0.0", 1, 1);
        checkDecimal(new Decimal(new BigDecimal("0")), "0", 1, 0);
        checkDecimal(new Decimal(new BigDecimal("1.0")), "1.0", 2, 1);
        checkDecimal(new Decimal(new BigDecimal("-0.09")), "-0.09", 2, 2);
        checkDecimal(new Decimal(new BigDecimal("-0.9")), "-0.9", 1, 1);
        checkDecimal(new Decimal(new BigDecimal("-0.90")), "-0.90", 2, 2);
        checkDecimal(new Decimal(new BigDecimal("-1.0")), "-1.0", 2, 1);
        checkDecimal(new Decimal(new BigDecimal("10.030")), "10.030", 5, 3);
        checkDecimal(new Decimal(new BigDecimal("10.030"), 4, 1), "10.0", 4, 1);
        checkDecimal(new Decimal(new BigDecimal("-9.95"), 4, 1), "-10.0", 4, 1);
        checkDecimal(new Decimal("10.030"), "10.030", 5, 3);
        checkDecimal(new Decimal(10.03), "10.03", 4, 2);
        checkDecimal(new Decimal(17L), "17", 20, 0);
        checkDecimal(new Decimal(17), "17", 10, 0);
        checkDecimal(new Decimal(17L, 2, 1), "1.7", 2, 1);
        checkDecimal(new Decimal(170L, 4, 2), "1.70", 4, 2);
        checkDecimal(new Decimal(17L, 24, 1), "1.7", 24, 1);
        checkDecimal(new Decimal((long)1e17, 18, 0), String.valueOf((long)1e17), 18, 0);
        checkDecimal(new Decimal(1000000000000000000L, 20, 2), "10000000000000000.00", 20, 2);
        checkDecimal(new Decimal(Long.MAX_VALUE), String.valueOf(Long.MAX_VALUE), 20, 0);
        checkDecimal(new Decimal(Long.MIN_VALUE), String.valueOf(Long.MIN_VALUE), 20, 0);
    }

    /** Check that a Decimal converts to the given double and long values */
    private void checkValues(Decimal d, double doubleValue, long longValue) {
        assertEquals(doubleValue, d.toDouble(), 1e-10);
        assertEquals(longValue, d.toLong());
    }

    @Test
    public void testDoubleAndLongValues() {
        checkValues(new Decimal(), 0.0, 0L);
        checkValues(new Decimal(new BigDecimal("10.030")), 10.03, 10L);
        checkValues(new Decimal(new BigDecimal("10.030"), 4, 1), 10.0, 10L);
        checkValues(new Decimal(new BigDecimal("-9.95"), 4, 1), -10.0, -10L);
        checkValues(new Decimal(10.03), 10.03, 10L);
        checkValues(new Decimal(17L), 17.0, 17L);
        checkValues(new Decimal(17), 17.0, 17L);
        checkValues(new Decimal(17L, 2, 1), 1.7, 1L);
        checkValues(new Decimal(170L, 4, 2), 1.7, 1L);
        checkValues(new Decimal((long)1e16), 1e16, (long)1e16);
        checkValues(new Decimal((long)1e17), 1e17, (long)1e17);
        checkValues(new Decimal((long)1e18), 1e18, (long)1e18);
        checkValues(new Decimal((long)2e18), 2e18, (long)2e18);
        checkValues(new Decimal(Long.MAX_VALUE), (double)Long.MAX_VALUE, Long.MAX_VALUE);
        checkValues(new Decimal(Long.MIN_VALUE), (double)Long.MIN_VALUE, Long.MIN_VALUE);
        checkValues(new Decimal(Double.MAX_VALUE), Double.MAX_VALUE, 0L);
        checkValues(new Decimal(Double.MIN_VALUE), Double.MIN_VALUE, 0L);
    }

    /**
     * Accessor for the BigDecimal value of a Decimal, which will be null if it's using Longs.
     * Uses reflection to access the private decimalVal field.
     */
    private BigDecimal getDecimalVal(Decimal d) {
        try {
            return (BigDecimal) DECIMAL_VAL_FIELD.get(d);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access decimalVal field", e);
        }
    }

    /** Check whether a decimal is represented compactly (passing whether we expect it to be) */
    private void checkCompact(Decimal d, boolean expected) {
        boolean isCompact = getDecimalVal(d) == null;
        assertTrue(isCompact == expected, 
            String.format("%s %s compact", d, expected ? "was not" : "was"));
    }

    @Test
    public void testSmallDecimalsRepresentedAsUnscaledLong() {
        checkCompact(new Decimal(), true);
        checkCompact(new Decimal(new BigDecimal("10.03")), false);
        checkCompact(new Decimal(new BigDecimal("100000000000000000000")), false);
        checkCompact(new Decimal(17L), true);
        checkCompact(new Decimal(17), true);
        checkCompact(new Decimal(17L, 2, 1), true);
        checkCompact(new Decimal(170L, 4, 2), true);
        checkCompact(new Decimal(17L, 24, 1), true);
        checkCompact(new Decimal((long)1e16), true);
        checkCompact(new Decimal((long)1e17), true);
        checkCompact(new Decimal((long)1e18 - 1), true);
        checkCompact(new Decimal(-(long)1e18 + 1), true);
        checkCompact(new Decimal((long)1e18 - 1, 30, 10), true);
        checkCompact(new Decimal(-(long)1e18 + 1, 30, 10), true);
        checkCompact(new Decimal((long)1e18), false);
        checkCompact(new Decimal(-(long)1e18), false);
        checkCompact(new Decimal((long)1e18, 30, 10), false);
        checkCompact(new Decimal(-(long)1e18, 30, 10), false);
        checkCompact(new Decimal(Long.MAX_VALUE), false);
        checkCompact(new Decimal(Long.MIN_VALUE), false);
    }

    @Test
    public void testEquals() {
        // The decimals on the left are stored compactly, while the ones on the right aren't
        checkCompact(new Decimal(123), true);
        checkCompact(new Decimal(new BigDecimal(123)), false);
        checkCompact(new Decimal("123"), false);
        assertEquals(new Decimal(123), new Decimal(new BigDecimal(123)));
        assertEquals(new Decimal(123), new Decimal(new BigDecimal("123.00")));
        assertEquals(new Decimal(-123), new Decimal(new BigDecimal(-123)));
        assertEquals(new Decimal(-123), new Decimal(new BigDecimal("-123.00")));
    }

    @Test
    public void testIsZero() {
        assertTrue(new Decimal(0).isZero());
        assertTrue(new Decimal(0, 4, 2).isZero());
        assertTrue(new Decimal("0").isZero());
        assertTrue(new Decimal("0.000").isZero());
        assertFalse(new Decimal(1).isZero());
        assertFalse(new Decimal(1, 4, 2).isZero());
        assertFalse(new Decimal("1").isZero());
        assertFalse(new Decimal("0.001").isZero());
    }

}