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

    // The decimal types compatible with other numeric types
    public static final DecimalType BooleanDecimal = new DecimalType(1, 0);
    public static final DecimalType ByteDecimal = new DecimalType(3, 0);
    public static final DecimalType ShortDecimal = new DecimalType(5, 0);
    public static final DecimalType IntDecimal = new DecimalType(10, 0);
    public static final DecimalType LongDecimal = new DecimalType(20, 0);
    public static final DecimalType FloatDecimal = new DecimalType(14, 7);
    public static final DecimalType DoubleDecimal = new DecimalType(30, 15);
    public static final DecimalType BigIntDecimal = new DecimalType(38, 0);

    /**
     * Returns the DecimalType that is compatible with the given DataType.
     */
    public static DecimalType forType(DataType dataType) {
        if (dataType instanceof IntegerType) {
            return IntDecimal;
        } else if (dataType instanceof LongType) {
            return LongDecimal;
        } else if (dataType instanceof FloatType) {
            return FloatDecimal;
        } else if (dataType instanceof DoubleType) {
            return DoubleDecimal;
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + dataType);
        }
    }

    /**
     * Returns whether this DecimalType is wider than `other`. If yes, it means `other`
     * can be casted into `this` safely without losing any precision or range.
     */
    public boolean isWiderThan(DataType other) {
        return isWiderThanInternal(other);
    }

    private boolean isWiderThanInternal(DataType other) {
        if (other instanceof DecimalType otherDecimal) {
            return (precision - scale) >= (otherDecimal.precision - otherDecimal.scale) && 
                   scale >= otherDecimal.scale;
        } else if (other instanceof IntegralType) {
            return isWiderThanInternal(forType(other));
        } else {
            return false;
        }
    }

    /**
     * Returns whether this DecimalType is tighter than `other`. If yes, it means `this`
     * can be casted into `other` safely without losing any precision or range.
     */
    public boolean isTighterThan(DataType other) {
        if (other instanceof DecimalType otherDecimal) {
            return (precision - scale) <= (otherDecimal.precision - otherDecimal.scale) && 
                   scale <= otherDecimal.scale;
        } else if (other instanceof IntegralType) {
            DecimalType integerAsDecimal = forType(other);
            assert integerAsDecimal.scale == 0 : "Integer as decimal should have scale 0";
            // If the precision equals `integerAsDecimal.precision`, there can be integer overflow
            // during casting.
            return precision < integerAsDecimal.precision && scale == 0;
        } else {
            return false;
        }
    }
}
