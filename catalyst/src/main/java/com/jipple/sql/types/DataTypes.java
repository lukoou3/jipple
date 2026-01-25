package com.jipple.sql.types;

public class DataTypes {
    public static final StringType STRING = StringType.INSTANCE;
    public static final IntegerType INTEGER = IntegerType.INSTANCE;
    public static final LongType LONG = LongType.INSTANCE;
    public static final FloatType FLOAT = FloatType.INSTANCE;
    public static final DoubleType DOUBLE = DoubleType.INSTANCE;
    public static final BooleanType BOOLEAN = BooleanType.INSTANCE;
    public static final BinaryType BINARY = BinaryType.INSTANCE;
    public static final DateType DATE = DateType.INSTANCE;
    public static final TimestampType TIMESTAMP = TimestampType.INSTANCE;
    public static final TimestampNTZType TIMESTAMP_NTZ = TimestampNTZType.INSTANCE;
    public static final NullType NULL = NullType.INSTANCE;

    public static final AbstractDataType NUMERIC = new AbstractDataType() {

        @Override
        public DataType defaultConcreteType() {
            return DataTypes.DOUBLE;
        }

        @Override
        public boolean acceptsType(DataType other) {
            return other instanceof NumericType;
        }

        @Override
        public String simpleString() {
            return "numeric";
        }
    };

    public static final AbstractDataType INTEGRAL = new AbstractDataType() {
        @Override
        public DataType defaultConcreteType() {
            return INTEGER;
        }

        @Override
        public boolean acceptsType(DataType other) {
            return other instanceof IntegralType;
        }

        @Override
        public String simpleString() {
            return "integral";
        }
    };

    public static final AbstractDataType ANY = new AbstractDataType() {

        @Override
        public DataType defaultConcreteType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean acceptsType(DataType other) {
            return true;
        }

        @Override
        public String simpleString() {
            return "any";
        }
    };
}
