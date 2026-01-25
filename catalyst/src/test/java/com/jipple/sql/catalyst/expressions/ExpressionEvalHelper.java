package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.SQLConf;
import com.jipple.sql.catalyst.CatalystTypeConverters;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.rule.ResolveTimeZone;
import com.jipple.sql.catalyst.expressions.named.Alias;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.MapData;
import com.jipple.sql.types.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class ExpressionEvalHelper {

    protected InternalRow createRow(Object... values) {
        Object[] converted = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            converted[i] = CatalystTypeConverters.convertToCatalyst(values[i]);
        }
        return InternalRow.of(converted);
    }

    protected Expression replace(Expression expr) {
        if (expr instanceof RuntimeReplaceable r) {
            return replace(r.replacement());
        } else {
            return expr.mapChildren(this::replace);
        }
    }

    protected Expression prepareEvaluation(Expression expression) {
        ResolveTimeZone resolver = new ResolveTimeZone();
        Expression expr = resolver.resolveTimeZones(replace(expression));
        assert expr.resolved();
        return copyBySerialize(expr);
    }

    protected void checkEvaluation(Expression expression, Object expected) {
        checkEvaluation(expression, expected, InternalRow.EMPTY);
    }

    protected void checkEvaluation(Expression expression, Object expected, InternalRow inputRow) {
        Expression expr = prepareEvaluation(expression);
        Object catalystValue = CatalystTypeConverters.convertToCatalyst(expected);
        checkEvaluationWithoutCodegen(expr, catalystValue, inputRow);
        expr = prepareEvaluation(expression);
        checkEvaluationWithSafeProjection(expr, catalystValue, inputRow);
    }

/*


  protected def checkEvaluation(
      expression: => Expression, expected: Any, inputRow: InternalRow = EmptyRow): Unit = {
    // Make it as method to obtain fresh expression everytime.
    def expr = prepareEvaluation(expression)
    val catalystValue = CatalystTypeConverters.convertToCatalyst(expected)
    checkEvaluationWithoutCodegen(expr, catalystValue, inputRow)
    checkEvaluationWithMutableProjection(expr, catalystValue, inputRow)
    if (GenerateUnsafeProjection.canSupport(expr.dataType)) {
      checkEvaluationWithUnsafeProjection(expr, catalystValue, inputRow)
    }
    checkEvaluationWithOptimization(expr, catalystValue, inputRow)
  }

* */

    protected Object evaluateWithoutCodegen(Expression expression, InternalRow inputRow) {
        expression.foreach(e -> {
            if (e instanceof RichExpression r) {
                try {
                    r.open(1, 0);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        return expression.eval(inputRow);
    }

    protected void checkEvaluationWithoutCodegen(Expression expression, Object expected, InternalRow inputRow) {
        Object actual;
        try {
            actual = evaluateWithoutCodegen(expression, inputRow);
        } catch (Exception e) {
            throw new RuntimeException("Exception evaluating " + expression, e);
        }
        if (!checkResult(actual, expected, expression)) {
            String input = Objects.equals(inputRow, InternalRow.EMPTY)?  "" : ", input: "  + inputRow;
            throw new RuntimeException(
                    "Incorrect evaluation (codegen off): " + expression +
                            ", actual: " + actual +
                            ", expected: " + expected + input);
        }
    }


    protected void checkEvaluationWithSafeProjection(Expression expression, Object expected, InternalRow inputRow) {
        CodegenObjectFactoryMode[] modes = new CodegenObjectFactoryMode[]{CodegenObjectFactoryMode.NO_CODEGEN, CodegenObjectFactoryMode.CODEGEN_ONLY};
        for (CodegenObjectFactoryMode fallbackMode : modes) {
            withCodegenFactoryMode(fallbackMode, () -> {
                Object actual;
                try {
                    actual = evaluateWithSafeProjection(expression, inputRow);
                } catch (Exception e) {
                    throw new RuntimeException("Exception SafeProjection evaluating " + expression, e);
                }
                if (!checkResult(actual, expected, expression)) {
                    String input = Objects.equals(inputRow, InternalRow.EMPTY)?  "" : ", input: "  + inputRow;
                    throw new RuntimeException(
                            "Incorrect evaluation (fallback mode = " + fallbackMode +  "): " + expression +
                                    ", actual: " + actual +
                                    ", expected: " + expected + input);
                }
            });
        }
    }

    protected Object evaluateWithSafeProjection(Expression expression, InternalRow inputRow) throws Exception {
        Projection plan = SafeProjectionGenerator.get().create(List.of(new Alias(expression, "Optimized(" + expression + ")")));
        plan.open(1, 0);
        return plan.apply(inputRow).get(0, expression.dataType());
    }

    /**
     * Check the equality between result of expression and expected value, it will handle
     * Array[Byte], Spread[Double], MapData and Row. Also check whether nullable in expression is
     * true if result is null
     */
    protected boolean checkResult(Object result, Object expected, Expression expression) {
        return checkResult(result, expected, expression.dataType(), expression.nullable());
    }

    protected boolean checkResult(Object result, Object expected, DataType exprDataType, boolean exprNullable) {
        DataType dataType = exprDataType;
        // The result is null for a non-nullable expression
        assert dataType != null || exprNullable: "nullable should be true if result is null";        
        if (result instanceof byte[] && expected instanceof byte[]) {
            return Arrays.equals((byte[]) result, (byte[]) expected);
        } else if (result instanceof Double && expected instanceof Double) {
            Double expectedDouble = (Double) expected;
            Double resultDouble = (Double) result;
            return expectedDouble.isNaN() ? resultDouble.isNaN() : expectedDouble.equals(resultDouble);
        } else if (result instanceof Float && expected instanceof Float) {
            Float expectedFloat = (Float) expected;
            Float resultFloat = (Float) result;
            return expectedFloat.isNaN() ? resultFloat.isNaN() : expectedFloat.equals(resultFloat);
        } else if (result instanceof InternalRow resultRow && expected instanceof InternalRow expectedRow) {
            StructType st = (StructType) dataType;
            assert resultRow.numFields() == st.length() && expectedRow.numFields() == st.length() : "field count mismatch";
            for (int i = 0; i < st.length(); i++) {
                StructField f = st.apply(i);
                if (!checkResult(
                        resultRow.get(i, f.dataType),
                        expectedRow.get(i, f.dataType),
                        f.dataType,
                        f.nullable)) {
                    return false;
                }
            }
            return true;
        } else if (result instanceof ArrayData resultArray && expected instanceof ArrayData expectedArray) {
            if (resultArray.numElements() != expectedArray.numElements()) {
                return false;
            }
            ArrayType arrayType = (ArrayType) dataType;
            DataType elementType = arrayType.elementType;
            boolean containsNull = arrayType.containsNull;
            for (int i = 0; i < resultArray.numElements(); i++) {
                if (!checkResult(resultArray.get(i, elementType), expectedArray.get(i, elementType), elementType, containsNull)) {
                    return false;
                }
            }
            return true;
        } else if (result instanceof MapData resultMap && expected instanceof MapData expectedMap) {
            MapType mapType = (MapType) dataType;
            return checkResult(resultMap.keyArray(), expectedMap.keyArray(), 
                    new ArrayType(mapType.keyType, false), false) &&
                    checkResult(resultMap.valueArray(), expectedMap.valueArray(),
                    new ArrayType(mapType.valueType, mapType.valueContainsNull), false);
        } else {
            return result == null && expected == null || (result != null && result.equals(expected));
        }
    }

    protected void withCodegenFactoryMode(CodegenObjectFactoryMode fallbackMode, Runnable f) {
        CodegenObjectFactoryMode old = SQLConf.get().codegenFactoryMode();
        SQLConf.get().setConf(SQLConf.CODEGEN_FACTORY_MODE, fallbackMode);
        try {
            f.run();
        } finally {
            SQLConf.get().setConf(SQLConf.CODEGEN_FACTORY_MODE, old);
        }
    }

    protected <T> T copyBySerialize(T obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            out.flush();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                 ObjectInputStream in = new ObjectInputStream(bis)) {
                @SuppressWarnings("unchecked")
                T copy = (T) in.readObject();
                return copy;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
