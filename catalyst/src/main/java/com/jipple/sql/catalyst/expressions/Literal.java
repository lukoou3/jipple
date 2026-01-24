package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.JavaCode;
import com.jipple.sql.types.*;
import com.jipple.unsafe.types.UTF8String;

import static com.jipple.sql.types.DataTypes.*;

public class Literal extends LeafExpression {
    public final Object value;
    public final DataType dataType;

    public Literal(Object value, DataType dataType) {
        this.value = value;
        this.dataType = dataType;
        validateLiteralValue(value, dataType);
    }

    @Override
    public Object[] args() {
        return new Object[]{value, dataType};
    }

    @Override
    public boolean nullable() {
        return value == null;
    }

    @Override
    public DataType dataType() {
        return dataType;
    }

    @Override
    public Object eval(InternalRow input) {
        return value;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        String javaType = CodeGeneratorUtils.javaType(dataType);
        if (value == null) {
            return ExprCode.forNullValue(dataType);
        } else {
            if (dataType instanceof BooleanType
                    || dataType instanceof IntegerType
                    || dataType instanceof DateType) {
                return ExprCode.forNonNullValue(JavaCode.literal(value.toString(), dataType));
            } else if (dataType instanceof FloatType) {
                float v = (Float) value;
                if (Float.isNaN(v)) {
                    return ExprCode.forNonNullValue(JavaCode.literal("Float.NaN", dataType));
                } else if (v == Float.POSITIVE_INFINITY) {
                    return ExprCode.forNonNullValue(JavaCode.literal("Float.POSITIVE_INFINITY", dataType));
                } else if (v == Float.NEGATIVE_INFINITY) {
                    return ExprCode.forNonNullValue(JavaCode.literal("Float.NEGATIVE_INFINITY", dataType));
                } else {
                    return ExprCode.forNonNullValue(JavaCode.literal(value + "F", dataType));
                }
            } else if (dataType instanceof DoubleType) {
                double v = (Double) value;
                if (Double.isNaN(v)) {
                    return ExprCode.forNonNullValue(JavaCode.literal("Double.NaN", dataType));
                } else if (v == Double.POSITIVE_INFINITY) {
                    return ExprCode.forNonNullValue(JavaCode.literal("Double.POSITIVE_INFINITY", dataType));
                } else if (v == Double.NEGATIVE_INFINITY) {
                    return ExprCode.forNonNullValue(JavaCode.literal("Double.NEGATIVE_INFINITY", dataType));
                } else {
                    return ExprCode.forNonNullValue(JavaCode.literal(value + "D", dataType));
                }
            } else if (dataType instanceof TimestampType
                    || dataType instanceof TimestampNTZType
                    || dataType instanceof LongType) {
                return ExprCode.forNonNullValue(JavaCode.literal(value + "L", dataType));
            } else {
                String constRef = ctx.addReferenceObj("literal", value, javaType);
                return ExprCode.forNonNullValue(JavaCode.global(constRef, dataType));
            }
        }
    }

    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }

    @Override
    public String sql() {
        return toString();
    }

    private void validateLiteralValue(Object value, DataType dataType) {
        if (!doValidate(value, dataType)) {
            throw new IllegalArgumentException(String.format("Literal must have a corresponding value to %s but class %s found.", dataType, value.getClass()));
        }
    }

    private boolean doValidate(Object v, DataType dataType) {
        if (v == null) {
            return true;
        } else if (dataType instanceof BooleanType) {
            return v instanceof Boolean;
        } else if (dataType instanceof IntegerType) {
            return v instanceof Integer;
        } else if (dataType instanceof LongType) {
            return v instanceof Long;
        } else if (dataType instanceof DoubleType) {
            return v instanceof Double;
        } else if (dataType instanceof StringType) {
            return v instanceof UTF8String;
        }

        return false;
    }

    static final Literal TrueLiteral = new Literal(true, BOOLEAN);
    static final Literal FalseLiteral = new Literal(false, BOOLEAN);

    public static Literal of(Object v) {
        if (v == null) {
            return of(null, DataTypes.NULL);
        } else if (v instanceof Integer) {
            return of(v, DataTypes.INTEGER);
        } else if (v instanceof Long) {
            return of(v, DataTypes.LONG);
        } else if (v instanceof Double) {
            return of(v, DataTypes.DOUBLE);
        } else if (v instanceof String s) {
            return of(UTF8String.fromString(s), DataTypes.STRING);
        }  else if (v instanceof UTF8String) {
            return of(v, DataTypes.STRING);
        } else if (v instanceof Boolean) {
            return of(v, DataTypes.BOOLEAN);
        } else {
            throw new IllegalArgumentException("Unsupported literal type: " + v.getClass());
        }
    }

    public static Literal of(Object v, DataType dataType) {
        return new Literal(v, dataType);
    }

}
