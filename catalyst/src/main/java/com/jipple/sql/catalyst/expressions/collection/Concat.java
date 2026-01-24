package com.jipple.sql.catalyst.expressions.collection;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.ComplexTypeMergingExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.ArrayData;
import com.jipple.sql.catalyst.util.GenericArrayData;
import com.jipple.sql.catalyst.util.TypeUtils;
import com.jipple.sql.errors.QueryExecutionErrors;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.ArrayType;
import com.jipple.sql.types.BinaryType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.StringType;
import com.jipple.sql.types.TypeCollection;
import com.jipple.tuple.Tuple2;
import com.jipple.unsafe.array.ByteArrayMethods;
import com.jipple.unsafe.types.ByteArray;
import com.jipple.unsafe.types.UTF8String;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jipple.sql.types.DataTypes.BINARY;
import static com.jipple.sql.types.DataTypes.STRING;

public class Concat extends ComplexTypeMergingExpression {
    private static final AbstractDataType ARRAY = new AbstractDataType() {
        @Override
        public DataType defaultConcreteType() {
            return new ArrayType(STRING);
        }

        @Override
        public boolean acceptsType(DataType other) {
            return other instanceof ArrayType;
        }

        @Override
        public String simpleString() {
            return "array";
        }
    };

    private final List<Expression> children;
    private transient Function<InternalRow, Object> _doConcat;

    public Concat(List<Expression> children) {
        this.children = children;
    }

    private List<AbstractDataType> allowedTypes() {
        return List.of(STRING, BINARY, ARRAY);
    }

    @Override
    public Object[] args() {
        return new Object[]{children};
    }

    @Override
    public List<Expression> children() {
        return children;
    }

    @Override
    public boolean nullable() {
        return children.stream().anyMatch(Expression::nullable);
    }

    @Override
    public boolean foldable() {
        return children.stream().allMatch(Expression::foldable);
    }

    @Override
    public DataType dataType() {
        return children.isEmpty() ? STRING : super.dataType();
    }

    private boolean resultArrayElementNullable() {
        return ((ArrayType) dataType()).containsNull;
    }

    private String javaType() {
        return CodeGeneratorUtils.javaType(dataType());
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        if (children.isEmpty()) {
            return TypeCheckResult.typeCheckSuccess();
        } else {
            for (int i = 0; i < children.size(); i++) {
                Expression child = children.get(i);
                boolean typeOk = allowedTypes().stream().anyMatch(t -> t.acceptsType(child.dataType()));
                if (!typeOk) {
                    return TypeCheckResult.dataTypeMismatch(
                            "UNEXPECTED_INPUT_TYPE",
                            Map.ofEntries(
                                    Map.entry("paramIndex", String.valueOf(i + 1)),
                                    Map.entry("requiredType", new TypeCollection(allowedTypes()).simpleString()),
                                    Map.entry("inputSql", child.sql()),
                                    Map.entry("inputType", child.dataType().simpleString())
                            )
                    );
                }
            }
            return TypeUtils.checkForSameTypeInputExpr(
                    children.stream().map(Expression::dataType).collect(Collectors.toList()),
                    prettyName());
        }
    }

    @Override
    public Object eval(InternalRow input) {
        return doConcat().apply(input);
    }

    private java.util.function.Function<InternalRow, Object> doConcat() {
        if (_doConcat != null) {
            return _doConcat;
        }
        DataType dt = dataType();
        if (dt instanceof BinaryType) {
            _doConcat = input -> {
                byte[][] inputs = new byte[children.size()][];
                for (int i = 0; i < children.size(); i++) {
                    inputs[i] = (byte[]) children.get(i).eval(input);
                }
                return ByteArray.concat(inputs);
            };
        } else if (dt instanceof StringType) {
            _doConcat = input -> {
                UTF8String[] inputs = new UTF8String[children.size()];
                for (int i = 0; i < children.size(); i++) {
                    inputs[i] = (UTF8String) children.get(i).eval(input);
                }
                return UTF8String.concat(inputs);
            };
        } else if (dt instanceof ArrayType arrayType) {
            _doConcat = input -> {
                List<Object> rawInputs = new ArrayList<>(children.size());
                for (Expression child : children) {
                    rawInputs.add(child.eval(input));
                }
                if (rawInputs.contains(null)) {
                    return null;
                }

                List<ArrayData> arrayData = new ArrayList<>(rawInputs.size());
                long numberOfElements = 0L;
                for (Object value : rawInputs) {
                    ArrayData ad = (ArrayData) value;
                    arrayData.add(ad);
                    numberOfElements += ad.numElements();
                }

                if (numberOfElements > ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH) {
                    throw QueryExecutionErrors.concatArraysWithElementsExceedLimitError(numberOfElements);
                }

                Object[] finalData = new Object[(int) numberOfElements];
                int position = 0;
                for (ArrayData ad : arrayData) {
                    Object[] arr = ad.toArray(arrayType.elementType);
                    System.arraycopy(arr, 0, finalData, position, arr.length);
                    position += arr.length;
                }
                return new GenericArrayData(finalData);
            };
        } else {
            throw new UnsupportedOperationException("Unsupported data type for concat: " + dt);
        }
        return _doConcat;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        List<ExprCode> evals = children.stream()
                .map(child -> child.genCode(ctx))
                .collect(Collectors.toList());
        String args = ctx.freshName("args");
        String hasNull = ctx.freshName("hasNull");

        List<String> inputs = new ArrayList<>(evals.size());
        for (int i = 0; i < evals.size(); i++) {
            ExprCode eval = evals.get(i);
            boolean nullable = children.get(i).nullable();
            if (nullable) {
                inputs.add(CodeGeneratorUtils.template(
                        """
                                if (!${hasNull}) {
                                  ${evalCode}
                                  if (!${evalIsNull}) {
                                    ${args}[${index}] = ${evalValue};
                                  } else {
                                    ${hasNull} = true;
                                  }
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("hasNull", hasNull),
                                Map.entry("evalCode", eval.code),
                                Map.entry("evalIsNull", eval.isNull),
                                Map.entry("args", args),
                                Map.entry("index", i),
                                Map.entry("evalValue", eval.value)
                        )
                ));
            } else {
                inputs.add(CodeGeneratorUtils.template(
                        """
                                if (!${hasNull}) {
                                  ${evalCode}
                                  ${args}[${index}] = ${evalValue};
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("hasNull", hasNull),
                                Map.entry("evalCode", eval.code),
                                Map.entry("args", args),
                                Map.entry("index", i),
                                Map.entry("evalValue", eval.value)
                        )
                ));
            }
        }

        String codes = ctx.splitExpressionsWithCurrentInputs(
                inputs,
                "valueConcat",
                List.of(
                        Tuple2.of(javaType() + "[]", args),
                        Tuple2.of(CodeGeneratorUtils.JAVA_BOOLEAN, hasNull)
                ),
                CodeGeneratorUtils.JAVA_BOOLEAN,
                body -> CodeGeneratorUtils.template(
                        """
                                ${body}
                                return ${hasNull};
                                """,
                        Map.ofEntries(
                                Map.entry("body", body),
                                Map.entry("hasNull", hasNull)
                        )
                ),
                funcCalls -> funcCalls.stream()
                        .map(funcCall -> CodeGeneratorUtils.template(
                                "${hasNull} = ${funcCall};",
                                Map.ofEntries(
                                        Map.entry("hasNull", hasNull),
                                        Map.entry("funcCall", funcCall)
                                )
                        ))
                        .collect(Collectors.joining("\n"))
        );

        String concat;
        String initCode;
        DataType dt = dataType();
        if (dt instanceof BinaryType) {
            concat = ByteArray.class.getName() + ".concat";
            initCode = "byte[][] " + args + " = new byte[" + evals.size() + "][];";
        } else if (dt instanceof StringType) {
            concat = "UTF8String.concat";
            initCode = "UTF8String[] " + args + " = new UTF8String[" + evals.size() + "];";
        } else if (dt instanceof ArrayType arrayType) {
            concat = genCodeForArrays(ctx, arrayType.elementType, arrayType.containsNull, args);
            initCode = "ArrayData[] " + args + " = new ArrayData[" + evals.size() + "];";
        } else {
            throw new UnsupportedOperationException("Unsupported data type for concat: " + dt);
        }

        return ev.copy(Block.block(
                """
                        boolean ${hasNull} = false;
                        ${initCode}
                        ${codes}
                        ${javaType} ${value} = null;
                        if (!${hasNull}) {
                          ${value} = ${concat}(${args});
                        }
                        boolean ${isNull} = ${value} == null;
                        """,
                Map.ofEntries(
                        Map.entry("hasNull", hasNull),
                        Map.entry("initCode", initCode),
                        Map.entry("codes", codes),
                        Map.entry("javaType", javaType()),
                        Map.entry("value", ev.value),
                        Map.entry("concat", concat),
                        Map.entry("args", args),
                        Map.entry("isNull", ev.isNull)
                )
        ));
    }

    private Tuple2<String, String> genCodeForNumberOfElements(CodegenContext ctx, String args) {
        String numElements = ctx.freshName("numElements");
        String z = ctx.freshName("z");
        String code = CodeGeneratorUtils.template(
                """
                        long ${numElements} = 0L;
                        for (int ${z} = 0; ${z} < ${numChildren}; ${z}++) {
                          ${numElements} += ${args}[${z}].numElements();
                        }
                        """,
                Map.ofEntries(
                        Map.entry("numElements", numElements),
                        Map.entry("z", z),
                        Map.entry("numChildren", children.size()),
                        Map.entry("args", args)
                )
        );
        return Tuple2.of(code, numElements);
    }

    private String genCodeForArrays(
            CodegenContext ctx,
            DataType elementType,
            boolean checkForNull,
            String args) {
        String counter = ctx.freshName("counter");
        String arrayData = ctx.freshName("arrayData");
        String y = ctx.freshName("y");
        String z = ctx.freshName("z");

        Tuple2<String, String> numElements = genCodeForNumberOfElements(ctx, args);
        String numElemCode = numElements._1;
        String numElemName = numElements._2;

        String initialization = CodeGeneratorUtils.createArrayData(
                arrayData,
                elementType,
                numElemName,
                " " + prettyName() + " failed.");
        String assignment = CodeGeneratorUtils.createArrayAssignment(
                arrayData,
                elementType,
                args + "[" + y + "]",
                counter,
                z,
                checkForNull && resultArrayElementNullable());

        String concat = ctx.freshName("concat");
        String concatDef = CodeGeneratorUtils.template(
                """
                        private ArrayData ${concat}(ArrayData[] ${args}) {
                          ${numElemCode}
                          ${initialization}
                          int ${counter} = 0;
                          for (int ${y} = 0; ${y} < ${numChildren}; ${y}++) {
                            for (int ${z} = 0; ${z} < ${args}[${y}].numElements(); ${z}++) {
                              ${assignment}
                              ${counter}++;
                            }
                          }
                          return ${arrayData};
                        }
                        """,
                Map.ofEntries(
                        Map.entry("concat", concat),
                        Map.entry("args", args),
                        Map.entry("numElemCode", numElemCode),
                        Map.entry("initialization", initialization),
                        Map.entry("counter", counter),
                        Map.entry("y", y),
                        Map.entry("numChildren", children.size()),
                        Map.entry("z", z),
                        Map.entry("assignment", assignment),
                        Map.entry("arrayData", arrayData)
                )
        );

        return ctx.addNewFunction(concat, concatDef);
    }

    @Override
    public String toString() {
        return "concat(" + children.stream().map(Expression::toString).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public String sql() {
        return "concat(" + children.stream().map(Expression::sql).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new Concat(newChildren);
    }
}
