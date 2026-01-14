package com.jipple.sql.types;

public class DecimalType  extends FractionalType {
    public static final int MAX_PRECISION = 38;
    public final int precision;
    public final int scale;

    public DecimalType(int precision, int scale) {
        this.precision = precision;
        this.scale = scale;
        checkNegativeScale(scale);
        if (scale > precision) {
            throw new IllegalArgumentException("Decimal scale (" + scale + ") cannot be greater than precision (" + precision + ").");
        }

        if (precision > DecimalType.MAX_PRECISION) {
            throw new IllegalArgumentException(simpleString() + " can only support precision up to " + DecimalType.MAX_PRECISION);
        }
    }

    // default constructor for Java
    public DecimalType() {
        this(10);
    }

    public DecimalType(int precision) {
        this(precision, 0);
    }


    @Override
    public int defaultSize() {
        return precision <= Decimal.MAX_LONG_DIGITS? 8 : 16;
    }

    @Override
    public DataType asNullable() {
        return this;
    }

    @Override
    public String typeName() {
        return String.format("decimal(%d,%d)", precision, scale);
    }

    @Override
    public String simpleString() {
        return String.format("DecimalType(%d,%d)", precision, scale);
    }

    @Override
    public int hashCode() {
        return precision * 31 + scale;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        DecimalType other = (DecimalType) o;
        return precision == other.precision && scale == other.scale;
    }

    static void checkNegativeScale(int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(String.format("Negative scale is not allowed: %d. ", scale));
        }
    }
}
