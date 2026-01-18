package com.jipple.sql.types;

import com.jipple.unsafe.types.UTF8String;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 将原先的 Scala `Decimal` 翻译为 Java 版本。
 * <p>
 * - 当 `decimalVal` 非空时，使用 `BigDecimal` 保存完整值；
 * - 否则使用 `longVal / 10^_scale` 的紧凑表示。
 * <p>
 * 对 -1.0 ~ 1.0 之间的数，精度只统计小数点后的位数。
 */
public final class Decimal implements Comparable<Decimal>, Serializable {
    private BigDecimal decimalVal = null;
    private long longVal = 0L;
    private int _precision = 1;
    private int _scale = 0;

    public Decimal() {
    }

    public Decimal(double value) {
        set(BigDecimal.valueOf(value));
    }

    public Decimal(long value) {
        set(value);
    }

    public Decimal(int value) {
        set(value);
    }

    public Decimal(BigDecimal value) {
        set(value);
    }

    public Decimal(java.math.BigInteger value) {
        set(value);
    }

    public Decimal(BigDecimal value, int precision, int scale) {
        set(value, precision, scale);
    }

    public Decimal(long unscaled, int precision, int scale) {
        set(unscaled, precision, scale);
    }

    public Decimal(String value) {
        set(new BigDecimal(value));
    }

    /** 当前精度（位数） */
    public int precision() {
        return _precision;
    }

    /** 当前 scale（小数位数） */
    public int scale() {
        return _scale;
    }

    /** 将值设为给定 long，精度 20，scale 0。 */
    public Decimal set(long value) {
        if (value <= -POW_10[MAX_LONG_DIGITS] || value >= POW_10[MAX_LONG_DIGITS]) {
            // We can't represent this compactly as a long without risking overflow
            this.decimalVal = BigDecimal.valueOf(value);
            this.longVal = 0L;
        } else {
            this.decimalVal = null;
            this.longVal = value;
        }
        this._precision = 20;
        this._scale = 0;
        return this;
    }

    /** 将值设为给定 int，精度 10，scale 0。 */
    public Decimal set(int value) {
        this.decimalVal = null;
        this.longVal = value;
        this._precision = 10;
        this._scale = 0;
        return this;
    }

    /** 用给定未缩放 long、指定精度与 scale 设置 Decimal。 */
    public Decimal set(long unscaled, int precision, int scale) {
        if (setOrNull(unscaled, precision, scale) == null) {
            throw new ArithmeticException("Unscaled value too large for precision");
        }
        return this;
    }

    /**
     * 用给定未缩放 long 设置 Decimal，若溢出返回 null。
     */
    public Decimal setOrNull(long unscaled, int precision, int scale) {
        checkNegativeScale(scale);
        if (unscaled <= -POW_10[MAX_LONG_DIGITS] || unscaled >= POW_10[MAX_LONG_DIGITS]) {
            // We can't represent this compactly as a long without risking overflow
            if (precision < 19) {
                return null; // Requested precision is too low to represent this value
            }
            this.decimalVal = new BigDecimal(BigInteger.valueOf(unscaled), scale);
            this.longVal = 0L;
        } else {
            long p = POW_10[Math.min(precision, MAX_LONG_DIGITS)];
            if (unscaled <= -p || unscaled >= p) {
                return null; // Requested precision is too low to represent this value
            }
            this.decimalVal = null;
            this.longVal = unscaled;
        }
        this._precision = precision;
        this._scale = scale;
        return this;
    }

    /** 使用 BigDecimal 并指定精度与 scale。 */
    public Decimal set(BigDecimal decimal, int precision, int scale) {
        checkNegativeScale(scale);
        this.decimalVal = decimal.setScale(scale, RoundingMode.HALF_UP);
        if (this.decimalVal.precision() > precision) {
            throw new ArithmeticException(
                    String.format("Decimal precision %d exceeds max precision %d",
                            this.decimalVal.precision(), precision));
        }
        this.longVal = 0L;
        this._precision = precision;
        this._scale = scale;
        return this;
    }

    /** 使用 BigDecimal，沿用其精度与 scale。 */
    public Decimal set(BigDecimal decimal) {
        this.decimalVal = decimal;
        this.longVal = 0L;
        if (decimal.precision() < decimal.scale()) {
            this._precision = decimal.scale();
            this._scale = decimal.scale();
        } else if (decimal.scale() < 0 /* && !SqlApiConf.get.allowNegativeScaleOfDecimalEnabled*/) {
            this._precision = decimal.precision() - decimal.scale();
            this._scale = 0;
            // set scale to 0 to correct unscaled value
            this.decimalVal = decimal.setScale(0);
        } else {
            this._precision = decimal.precision();
            this._scale = decimal.scale();
        }
        return this;
    }

    /**
     * 使用 BigInteger，如果超出 long 范围则退化为 BigDecimal。
     */
    public Decimal set(BigInteger bigInteger) {
        try {
            this.decimalVal = null;
            this.longVal = bigInteger.longValueExact();
            this._precision = MAX_DECIMAL_PRECISION;
            this._scale = 0;
            return this;
        } catch (ArithmeticException e) {
            return set(new BigDecimal(bigInteger));
        }
    }

    /** 拷贝已有 Decimal 的值。 */
    public Decimal set(Decimal decimal) {
        this.decimalVal = decimal.decimalVal;
        this.longVal = decimal.longVal;
        this._precision = decimal._precision;
        this._scale = decimal._scale;
        return this;
    }

    public BigDecimal toBigDecimal() {
        return decimalVal != null ? decimalVal : new BigDecimal(BigInteger.valueOf(longVal), _scale);
    }

    public BigInteger toBigInteger() {
        if (decimalVal != null) {
            return decimalVal.unscaledValue();
        } else {
            return BigInteger.valueOf(actualLongVal());
        }
    }

    public long toUnscaledLong() {
        if (decimalVal != null) {
            return decimalVal.unscaledValue().longValueExact();
        } else {
            return longVal;
        }
    }

    @Override
    public String toString() {
        return toBigDecimal().toString();
    }

    public String toPlainString() {
        return toBigDecimal().toPlainString();
    }

    public String toDebugString() {
        if (decimalVal != null) {
            return String.format("Decimal(expanded, %s, %d, %d)", decimalVal, precision(), scale());
        } else {
            return String.format("Decimal(compact, %d, %d, %d)", longVal, precision(), scale());
        }
    }

    public double toDouble() {
        return toBigDecimal().doubleValue();
    }

    public float toFloat() {
        return toBigDecimal().floatValue();
    }

    private long actualLongVal() {
        return longVal / POW_10[_scale];
    }

    public long toLong() {
        return decimalVal == null ? actualLongVal() : decimalVal.longValue();
    }

    public int toInt() {
        return (int) toLong();
    }

    public short toShort() {
        return (short) toLong();
    }

    public byte toByte() {
        return (byte) toLong();
    }

    /**
     * 取整为 Int，溢出时抛异常。
     */
    int roundToInt() {
        return roundToNumeric("Int", Integer.MAX_VALUE, Integer.MIN_VALUE,
                Long::intValue, Double::intValue);
    }

    private String toSqlValue() {
        return this + "BD";
    }

    private <T> T roundToNumeric(String integralType, int maxValue, int minValue,
                                 java.util.function.Function<Long, T> f1,
                                 java.util.function.Function<Double, T> f2) {
        if (decimalVal == null) {
            long actual = actualLongVal();
            T numericVal = f1.apply(actual);
            if (((Number) numericVal).longValue() == actual) {
                return numericVal;
            } else {
                throw new ArithmeticException(String.format(
                        "The value %s of the type %s cannot be cast to %s due to an overflow. Use `try_cast` to tolerate overflow and return NULL instead.",
                        toSqlValue(), decimalTypeString(precision(), scale()), integralType));
            }
        } else {
            double doubleVal = decimalVal.doubleValue();
            if (Math.floor(doubleVal) <= maxValue && Math.ceil(doubleVal) >= minValue) {
                return f2.apply(doubleVal);
            } else {
                throw new ArithmeticException(String.format(
                        "The value %s of the type %s cannot be cast to %s due to an overflow. Use `try_cast` to tolerate overflow and return NULL instead.",
                        toSqlValue(), decimalTypeString(precision(), scale()), integralType));
            }
        }
    }

    /**
     * 取整为 Long，溢出时抛异常。
     */
    long roundToLong() {
        if (decimalVal == null) {
            return actualLongVal();
        } else {
            try {
                return decimalVal.toBigIntegerExact().longValueExact();
            } catch (ArithmeticException e) {
                throw new ArithmeticException(String.format(
                        "The value %s of the type %s cannot be cast to %s due to an overflow. Use `try_cast` to tolerate overflow and return NULL instead.",
                        toSqlValue(), decimalTypeString(precision(), scale()), "Long"));
            }
        }
    }

    /** 修改精度与 scale，保持数值不变，成功返回 true。 */
    public boolean changePrecision(int precision, int scale) {
        return changePrecision(precision, scale, RoundingMode.HALF_UP);
    }

    /**
     * 调整为指定精度与 scale。
     */
    Decimal toPrecision(int precision, int scale,
                        RoundingMode roundMode,
                        boolean nullOnOverflow) {
        Decimal copy = this.clone();
        if (copy.changePrecision(precision, scale, roundMode)) {
            return copy;
        } else if (nullOnOverflow) {
            return null;
        } else {
            throw new ArithmeticException(
                    String.format("%s cannot be represented as Decimal(%d, %d).",
                            toDebugString(), precision, scale));
        }
    }

    /**
     * 修改精度与 scale，保持数值不变，溢出返回 false。
     */
    boolean changePrecision(int precision, int scale, RoundingMode roundMode) {
        if (precision == this.precision() && scale == this.scale()) {
            return true;
        }
        checkNegativeScale(scale);
        long lv = longVal;
        BigDecimal dv = decimalVal;

        if (dv == null) {
            if (scale < _scale) {
                int diff = _scale - scale;
                if (diff > MAX_LONG_DIGITS) {
                    lv = adjustWhenAllDropped(lv, roundMode);
                } else {
                    long pow10diff = POW_10[diff];
                    long droppedDigits = lv % pow10diff;
                    lv /= pow10diff;
                    lv = applyRounding(lv, droppedDigits, pow10diff, roundMode);
                }
            } else if (scale > _scale) {
                int diff = scale - _scale;
                long p = POW_10[Math.max(MAX_LONG_DIGITS - diff, 0)];
                if (diff <= MAX_LONG_DIGITS && lv > -p && lv < p) {
                    lv *= POW_10[diff];
                } else {
                    dv = new BigDecimal(BigInteger.valueOf(lv), _scale);
                }
            }
        }

        if (dv != null) {
            dv = dv.setScale(scale, roundMode);
            if (dv.precision() > precision) {
                return false;
            }
        } else {
            long p = POW_10[Math.min(precision, MAX_LONG_DIGITS)];
            if (lv <= -p || lv >= p) {
                return false;
            }
        }
        decimalVal = dv;
        longVal = lv;
        _precision = precision;
        _scale = scale;
        return true;
    }

    private static long adjustWhenAllDropped(long lv, RoundingMode roundMode) {
        switch (roundMode) {
            case FLOOR:
                return lv < 0 ? -1L : 0L;
            case CEILING:
                return lv > 0 ? 1L : 0L;
            case HALF_UP:
            case HALF_EVEN:
                return 0L;
            default:
                throw new IllegalArgumentException("Not supported rounding mode: " + roundMode);
        }
    }

    private static long applyRounding(long lv, long droppedDigits, long pow10diff,
                                      RoundingMode roundMode) {
        switch (roundMode) {
            case FLOOR:
                if (droppedDigits < 0) lv += -1L;
                break;
            case CEILING:
                if (droppedDigits > 0) lv += 1L;
                break;
            case HALF_UP:
                if (Math.abs(droppedDigits) * 2 >= pow10diff) {
                    lv += droppedDigits < 0 ? -1L : 1L;
                }
                break;
            case HALF_EVEN:
                long doubled = Math.abs(droppedDigits) * 2;
                if (doubled > pow10diff || (doubled == pow10diff && lv % 2 != 0)) {
                    lv += droppedDigits < 0 ? -1L : 1L;
                }
                break;
            default:
                throw new IllegalArgumentException("Not supported rounding mode: " + roundMode);
        }
        return lv;
    }

    @Override
    public Decimal clone() {
        return new Decimal().set(this);
    }

    @Override
    public int compareTo(Decimal other) {
        if (decimalVal == null && other.decimalVal == null && _scale == other._scale) {
            return Long.compare(longVal, other.longVal);
        } else {
            return toBigDecimal().compareTo(other.toBigDecimal());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Decimal)) return false;
        return compareTo((Decimal) obj) == 0;
    }

    @Override
    public int hashCode() {
        return toBigDecimal().hashCode();
    }

    public boolean isZero() {
        return decimalVal != null ? decimalVal.signum() == 0 : longVal == 0;
    }

    // 加法：若同 scale 且均为 long 表示，则使用紧凑路径。
    public Decimal plus(Decimal that) {
        if (decimalVal == null && that.decimalVal == null && scale() == that.scale()) {
            return new Decimal(longVal + that.longVal,
                    Math.max(precision(), that.precision()) + 1, scale());
        } else {
            return new Decimal(toBigDecimal().add(that.toBigDecimal()));
        }
    }

    // 减法
    public Decimal minus(Decimal that) {
        if (decimalVal == null && that.decimalVal == null && scale() == that.scale()) {
            return new Decimal(longVal - that.longVal,
                    Math.max(precision(), that.precision()) + 1, scale());
        } else {
            return new Decimal(toBigDecimal().subtract(that.toBigDecimal()));
        }
    }

    // 乘法
    public Decimal times(Decimal that) {
        return new Decimal(toBigDecimal().multiply(that.toBigDecimal(), MATH_CONTEXT));
    }

    // 除法
    public Decimal div(Decimal that) {
        if (that.isZero()) return null;
        return new Decimal(toBigDecimal().divide(that.toBigDecimal(),
                MAX_DECIMAL_SCALE + 1, MATH_CONTEXT.getRoundingMode()));
    }

    // 取余
    public Decimal remainder(Decimal that) {
        if (that.isZero()) return null;
        return new Decimal(toBigDecimal().remainder(that.toBigDecimal(), MATH_CONTEXT));
    }

    // 整除
    public Decimal quot(Decimal that) {
        if (that.isZero()) return null;
        return new Decimal(toBigDecimal().divideToIntegralValue(that.toBigDecimal(), MATH_CONTEXT));
    }

    public Decimal unaryMinus() {
        if (decimalVal != null) {
            return new Decimal(decimalVal.negate(), precision(), scale());
        } else {
            return new Decimal(-longVal, precision(), scale());
        }
    }

    public Decimal abs() {
        return this.compareTo(Decimal.ZERO) < 0 ? unaryMinus() : this;
    }

    public Decimal floor() {
        if (scale() == 0) return this;
        int newPrecision = boundedPrecision(precision() - scale() + 1);
        return toPrecision(newPrecision, 0, RoundingMode.FLOOR, false);
    }

    public Decimal ceil() {
        if (scale() == 0) return this;
        int newPrecision = boundedPrecision(precision() - scale() + 1);
        return toPrecision(newPrecision, 0, RoundingMode.CEILING, false);
    }

    // ----- 静态成员与工厂方法 -----

    /** Decimal 支持的最大精度/scale（与 Spark 默认保持一致） */
    public static final int MAX_DECIMAL_PRECISION = 38;
    public static final int MAX_DECIMAL_SCALE = 38;

    /** Int 能表示的最大十进制位数 */
    public static final int MAX_INT_DIGITS = 9;
    /** Long 能表示的最大十进制位数 */
    public static final int MAX_LONG_DIGITS = 18;

    public static final long[] POW_10 = new long[MAX_LONG_DIGITS + 1];

    // MathContext：精度 +1 并向下取整，避免双重 HALF_UP 误差。
    private static final MathContext MATH_CONTEXT = new MathContext(MAX_DECIMAL_PRECISION + 1, RoundingMode.DOWN);

    public static final Decimal ZERO = new Decimal(0);
    public static final Decimal ONE = new Decimal(1);


    static {
        for (int i = 0; i <= MAX_LONG_DIGITS; i++) {
            POW_10[i] = (long) Math.pow(10, i);
        }
    }

    private static void checkNegativeScale(int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("scale cannot be negative");
        }
    }

    private static int boundedPrecision(int candidate) {
        if (candidate < 0) return 0;
        return Math.min(candidate, MAX_DECIMAL_PRECISION);
    }

    private static String decimalTypeString(int precision, int scale) {
        return "Decimal(" + precision + "," + scale + ")";
    }

    /** RowEncoder 等外部行支持。 */
    public static Decimal fromDecimal(Object value) {
        if (value instanceof BigDecimal) return new Decimal((BigDecimal) value);
        if (value instanceof BigInteger) return new Decimal((BigInteger) value);
        if (value instanceof Decimal) return (Decimal) value;
        throw new IllegalArgumentException("Unsupported decimal source: " + value);
    }

    private static int numDigitsInIntegralPart(BigDecimal bigDecimal) {
        return bigDecimal.precision() - bigDecimal.scale();
    }

    private static BigDecimal stringtoBigDecimal(UTF8String str) {
        return new BigDecimal(str.toString().trim());
    }

    public static Decimal fromString(UTF8String str) {
        try {
            BigDecimal bd = stringtoBigDecimal(str);
            if (numDigitsInIntegralPart(bd) > MAX_DECIMAL_PRECISION /* !SqlApiConf.get.allowNegativeScaleOfDecimalEnabled */) {
                return null;
            } else {
                return new Decimal(bd);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Decimal fromStringANSI(UTF8String str,
                                         Object to) {
        try {
            BigDecimal bd = stringtoBigDecimal(str);
            if (numDigitsInIntegralPart(bd) > MAX_DECIMAL_PRECISION /* !SqlApiConf.get.allowNegativeScaleOfDecimalEnabled */) {
                throw new ArithmeticException("out of decimal type range: " + str);
            } else {
                return new Decimal(bd);
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("invalid input syntax for type numeric: " + str);
        }
    }

    /** 创建未经边界检查的 Decimal。 */
    public static Decimal createUnsafe(long unscaled, int precision, int scale) {
        checkNegativeScale(scale);
        Decimal dec = new Decimal();
        dec.longVal = unscaled;
        dec._precision = precision;
        dec._scale = scale;
        return dec;
    }

    /** 返回能在 numBytes 字节中存储的最大精度。 */
    public static int maxPrecisionForBytes(int numBytes) {
        return (int) Math.round(
                Math.floor(Math.log10(Math.pow(2, 8 * numBytes - 1) - 1)));
    }

    /** 给定精度所需的最少字节数（缓存）。 */
    public static final int[] minBytesForPrecision = computeMinBytesArray();

    private static int[] computeMinBytesArray() {
        int[] arr = new int[39];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = computeMinBytesForPrecision(i);
        }
        return arr;
    }

    private static int computeMinBytesForPrecision(int precision) {
        int numBytes = 1;
        while (Math.pow(2.0, 8 * numBytes - 1) < Math.pow(10.0, precision)) {
            numBytes += 1;
        }
        return numBytes;
    }
}

