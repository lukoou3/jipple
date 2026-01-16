package com.jipple.sql.types;

public class DataTypes {
    public static final DataType STRING = StringType.INSTANCE;
    public static final DataType INTEGER = IntegerType.INSTANCE;
    public static final DataType LONG = LongType.INSTANCE;
    public static final DataType FLOAT = FloatType.INSTANCE;
    public static final DataType DOUBLE = DoubleType.INSTANCE;
    public static final DataType BOOLEAN = BooleanType.INSTANCE;
    public static final DataType BINARY = BinaryType.INSTANCE;
    public static final DataType DATE = DateType.INSTANCE;
    public static final DataType TIMESTAMP = TimestampType.INSTANCE;
    public static final DataType TIMESTAMP_NTZ = TimestampNTZType.INSTANCE;
    public static final DataType NULL = NullType.INSTANCE;

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
