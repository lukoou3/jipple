package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.EquivalentExpressions;
import com.jipple.sql.SQLConf;
import com.jipple.sql.catalyst.expressions.ExpressionEquals;
import com.jipple.sql.catalyst.util.SQLOrderingUtil;
import com.jipple.sql.types.*;
import com.jipple.tuple.Tuple2;
import com.jipple.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jipple.util.Utils.assertCondition;

/**
 * A context for codegen, tracking a list of objects that could be passed into generated Java
 * function.
 */
public class CodegenContext {
    private static final Logger logger = LoggerFactory.getLogger(CodegenContext.class);

    /**
     * Holding a list of objects that could be used passed into generated class.
     */
    public final List<Object> references = new ArrayList<>();

    /**
     * Holding the variable name of the input row of the current operator, will be used by
     * `BoundReference` to generate code.
     *
     * Note that if `currentVars` is not null, `BoundReference` prefers `currentVars` over `INPUT_ROW`
     * to generate code. If you want to make sure the generated code use `INPUT_ROW`, you need to set
     * `currentVars` to null, or set `currentVars(i)` to null for certain columns, before calling
     * `Expression.genCode`.
     */
    public String INPUT_ROW = "i";

    /**
     * Holding a list of generated columns as input of current operator, will be used by
     * BoundReference to generate code.
     */
    public List<ExprCode> currentVars = null;

    /**
     * Holding expressions' inlined mutable states like `MonotonicallyIncreasingID.count` as a
     * 2-tuple: java type, variable name.
     * As an example, ("int", "count") will produce code:
     *   private int count;
     * as a member variable
     *
     * They will be kept as member variables in generated classes like `SpecificProjection`.
     *
     * Exposed for tests only.
     */
    public final List<Tuple2<String, String>> inlinedMutableStates = new ArrayList<>();

    /**
     * The mapping between mutable state types and corresponding compacted arrays.
     * The keys are java type string. The values are [[MutableStateArrays]] which encapsulates
     * the compacted arrays for the mutable states with the same java type.
     *
     * Exposed for tests only.
     */
    public final Map<String, MutableStateArrays> arrayCompactedMutableStates = new HashMap<>();

    // An array holds the code that will initialize each state
    // Exposed for tests only.
    public final List<String> mutableStateInitCode = new ArrayList<>();

    // Tracks the names of all the mutable states.
    private final Set<String> mutableStateNames = new HashSet<>();

    /**
     * A map containing the mutable states which have been defined so far using
     * `addImmutableStateIfNotExists`. Each entry contains the name of the mutable state as key and
     * its Java type and init code as value.
     */
    private final Map<String, Tuple2<String, String>> immutableStates = new HashMap<>();

    /**
     * Code statements to initialize states that depend on the partition index.
     * An integer `partitionIndex` will be made available within the scope.
     */
    public final List<String> partitionInitializationStatements = new ArrayList<>();

    public final List<String> partitionClosureStatements = new ArrayList<>();

    /**
     * Holds expressions that are equivalent. Used to perform subexpression elimination
     * during codegen.
     *
     * For expressions that appear more than once, generate additional code to prevent
     * recomputing the value.
     *
     * For example, consider two expression generated from this SQL statement:
     *  SELECT (col1 + col2), (col1 + col2) / col3.
     *
     *  equivalentExpressions will match the tree containing `col1 + col2` and it will only
     *  be evaluated once.
     */
    private final EquivalentExpressions equivalentExpressions = new EquivalentExpressions();

    // Foreach expression that is participating in subexpression elimination, the state to use.
    // Visible for testing.
    public Map<ExpressionEquals, SubExprEliminationState> subExprEliminationExprs = new HashMap<>();

    // The collection of sub-expression result resetting methods that need to be called on each row.
    private final List<String> subexprFunctions = new ArrayList<>();

    public final String outerClassName = "OuterClass";

    /**
     * Holds the class and instance names to be generated, where `OuterClass` is a placeholder
     * standing for whichever class is generated as the outermost class and which will contain any
     * inner sub-classes. All other classes and instance names in this list will represent private,
     * inner sub-classes.
     */
    private final List<Tuple2<String, String>> classes = new ArrayList<>();

    // A map holding the current size in bytes of each class to be generated.
    private final Map<String, Integer> classSize = new HashMap<>();

    // Nested maps holding function names and their code belonging to each class.
    private final Map<String, Map<String, String>> classFunctions = new HashMap<>();

    // Verbatim extra code to be added to the OuterClass.
    private final List<String> extraClasses = new ArrayList<>();

    /**
     * The map from a variable name to it's next ID.
     */
    private final Map<String, Integer> freshNameIds = new HashMap<>();

    /**
     * A prefix used to generate fresh name.
     */
    public String freshNamePrefix = "";

    /**
     * The map from a place holder to a corresponding comment
     */
    private final Map<String, String> placeHolderToComments = new HashMap<>();

    public CodegenContext() {
        classes.add(Tuple2.of(outerClassName, null));
        classSize.put(outerClassName, 0);
        classFunctions.put(outerClassName, new HashMap<>());
        freshNameIds.put(INPUT_ROW, 1);
    }

    /**
     * Returns the reference objects as an array for generated classes.
     */
    public Object[] referencesArray() {
        return references.toArray();
    }

    /**
     * Add an object to `references`.
     *
     * Returns the code to access it.
     *
     * This does not to store the object into field but refer it from the references field at the
     * time of use because number of fields in class is limited so we should reduce it.
     */
    public String addReferenceObj(String objName, Object obj, String className) {
        int idx = references.size();
        references.add(obj);
        String clsName = className != null ? className : CodeGeneratorUtils.typeName(obj.getClass());
        return CodeGeneratorUtils.template(
                "((${clsName}) references[${idx}] /* ${objName} */)",
                Map.of(
                        "clsName", clsName,
                        "idx", idx,
                        "objName", objName
                )
        );
    }

    public String addReferenceObj(String objName, Object obj) {
        return addReferenceObj(objName, obj, null);
    }

    /**
     * This class holds a set of names of mutableStateArrays that is used for compacting mutable
     * states for a certain type, and holds the next available slot of the current compacted array.
     */
    public class MutableStateArrays {
        public final List<String> arrayNames = new ArrayList<>();
        private int currentIndex = 0;

        public MutableStateArrays() {
            createNewArray();
        }

        private void createNewArray() {
            String newArrayName = freshName("mutableStateArray");
            mutableStateNames.add(newArrayName);
            arrayNames.add(newArrayName);
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        /**
         * Returns the reference of next available slot in current compacted array. The size of each
         * compacted array is controlled by the constant `MUTABLESTATEARRAY_SIZE_LIMIT`.
         * Once reaching the threshold, new compacted array is created.
         */
        public String getNextSlot() {
            if (currentIndex < CodeGeneratorUtils.MUTABLESTATEARRAY_SIZE_LIMIT) {
                String res = CodeGeneratorUtils.template(
                        "${arrayName}[${index}]",
                        Map.of(
                                "arrayName", arrayNames.get(arrayNames.size() - 1),
                                "index", currentIndex
                        )
                );
                currentIndex++;
                return res;
            } else {
                createNewArray();
                currentIndex = 1;
                return CodeGeneratorUtils.template(
                        "${arrayName}[0]",
                        Map.of("arrayName", arrayNames.get(arrayNames.size() - 1))
                );
            }
        }
    }

    /**
     * Add a mutable state as a field to the generated class. c.f. the comments above.
     *
     * @param javaType Java type of the field. Note that short names can be used for some types,
     *                 e.g. InternalRow, UnsafeRow, UnsafeArrayData, etc. Other types will have to
     *                 specify the fully-qualified Java type name. Generic type arguments are accepted
     *                 but ignored.
     * @param variableName Name of the field.
     * @param initFunc Function includes statement(s) to put into the init() method to initialize
     *                 this field. The argument is the name of the mutable state variable.
     *                 If left blank, the field will be default-initialized.
     * @param forceInline whether the declaration and initialization code may be inlined rather than
     *                    compacted. Please set {@code true} into forceInline for one of the followings:
     *                    1. use the original name of the status
     *                    2. expect to non-frequently generate the status
     *                       (e.g. not much sort operators in one stage)
     * @param useFreshName If this is false and the mutable state ends up inlining in the outer
     *                     class, the name is not changed
     * @return the name of the mutable state variable, which is the original name or fresh name if
     *         the variable is inlined to the outer class, or an array access if the variable is to
     *         be stored in an array of variables of the same type.
     */
    public String addMutableState(
            String javaType,
            String variableName,
            Function<String, String> initFunc,
            boolean forceInline,
            boolean useFreshName) {

        // want to put a primitive type variable at outerClass for performance
        boolean canInlinePrimitive = CodeGeneratorUtils.isPrimitiveType(javaType) &&
                (inlinedMutableStates.size() < CodeGeneratorUtils.OUTER_CLASS_VARIABLES_THRESHOLD);
        if (forceInline || canInlinePrimitive || javaType.contains("[][]")) {
            String varName = useFreshName ? freshName(variableName) : variableName;
            String initCode = initFunc.apply(varName);
            inlinedMutableStates.add(Tuple2.of(javaType, varName));
            mutableStateInitCode.add(initCode);
            mutableStateNames.add(varName);
            return varName;
        } else {
            MutableStateArrays arrays = arrayCompactedMutableStates.computeIfAbsent(javaType, k -> new MutableStateArrays());
            String element = arrays.getNextSlot();
            String initCode = initFunc.apply(element);
            mutableStateInitCode.add(initCode);
            return element;
        }
    }

    public String addMutableState(String javaType, String variableName) {
        return addMutableState(javaType, variableName, s -> "", false, true);
    }

    public String addMutableState(String javaType, String variableName, Function<String, String> initFunc) {
        return addMutableState(javaType, variableName, initFunc, false, true);
    }

    public String addMutableState(String javaType, String variableName, Function<String, String> initFunc, boolean forceInline) {
        return addMutableState(javaType, variableName, initFunc, forceInline, true);
    }

    /**
     * Add an immutable state as a field to the generated class only if it does not exist yet a field
     * with that name. This helps reducing the number of the generated class' fields, since the same
     * variable can be reused by many functions.
     *
     * Even though the added variables are not declared as final, they should never be reassigned in
     * the generated code to prevent errors and unexpected behaviors.
     *
     * Internally, this method calls {@link #addMutableState}.
     */
    public void addImmutableStateIfNotExists(
            String javaType,
            String variableName,
            Function<String, String> initFunc) {
        Tuple2<String, String> existingImmutableState = immutableStates.get(variableName);
        if (existingImmutableState == null) {
            addMutableState(javaType, variableName, initFunc, true, false);
            immutableStates.put(variableName, Tuple2.of(javaType, initFunc.apply(variableName)));
        } else {
            String prevJavaType = existingImmutableState._1;
            String prevInitCode = existingImmutableState._2;
            assertCondition(prevJavaType.equals(javaType), () -> variableName + " has already been defined with type " +
                    prevJavaType + " and now it is tried to define again with type " + javaType + ".");
            assertCondition(prevInitCode.equals(initFunc.apply(variableName)), () ->  variableName + " has already been defined " +
                    "with different initialization statements.");
        }
    }

    public void addImmutableStateIfNotExists(String javaType, String variableName) {
        addImmutableStateIfNotExists(javaType, variableName, s -> "");
    }

    /**
     * Add buffer variable which stores data coming from an {@link com.jipple.sql.catalyst.InternalRow}.
     * This method guarantees that the variable is safely stored, which is important for (potentially)
     * byte array backed data types like: UTF8String, ArrayData, MapData & InternalRow.
     */
    public ExprCode addBufferedState(DataType dataType, String variableName, String initCode) {
        String value = addMutableState(CodeGeneratorUtils.javaType(dataType), variableName);
        Block code;
        // 本来就不支持UserDefinedType类型
        DataType sqlType = dataType; // UserDefinedType.sqlType(dataType);
        if (sqlType instanceof StringType) {
            code = Block.block(
                    "${value} = ${initCode}.clone();",
                    Map.of("value", value, "initCode", initCode)
            );
        } else if (sqlType instanceof StructType || sqlType instanceof ArrayType || sqlType instanceof MapType) {
            code = Block.block(
                    "${value} = ${initCode}.copy();",
                    Map.of("value", value, "initCode", initCode)
            );
        } else {
            code = Block.block(
                    "${value} = ${initCode};",
                    Map.of("value", value, "initCode", initCode)
            );
        }
        return new ExprCode(code, FalseLiteral.INSTANCE, JavaCode.global(value, dataType));
    }

    public String declareMutableStates() {
        // It's possible that we add same mutable state twice, e.g. the `mergeExpressions` in
        // `TypedAggregateExpression`, we should call `distinct` here to remove the duplicated ones.
        List<String> inlinedStates = inlinedMutableStates.stream()
                .distinct()
                .map(t -> CodeGeneratorUtils.template(
                        "private ${javaType} ${variableName};",
                        Map.of("javaType", t._1, "variableName", t._2)
                ))
                .collect(Collectors.toList());

        List<String> arrayStates = new ArrayList<>();
        for (Map.Entry<String, MutableStateArrays> entry : arrayCompactedMutableStates.entrySet()) {
            String javaType = entry.getKey();
            MutableStateArrays mutableStateArrays = entry.getValue();
            int numArrays = mutableStateArrays.arrayNames.size();
            for (int index = 0; index < mutableStateArrays.arrayNames.size(); index++) {
                String arrayName = mutableStateArrays.arrayNames.get(index);
                int length = (index + 1 == numArrays) ?
                        mutableStateArrays.getCurrentIndex() :
                        CodeGeneratorUtils.MUTABLESTATEARRAY_SIZE_LIMIT;
                String stateCode;
                if (javaType.contains("[]")) {
                    // initializer had an one-dimensional array variable
                    String baseType = javaType.substring(0, javaType.length() - 2);
                    stateCode = CodeGeneratorUtils.template(
                            "private ${javaType}[] ${arrayName} = new ${baseType}[${length}][];",
                            Map.of(
                                    "javaType", javaType,
                                    "arrayName", arrayName,
                                    "baseType", baseType,
                                    "length", length
                            )
                    );
                } else {
                    // initializer had a scalar variable
                    stateCode = CodeGeneratorUtils.template(
                            "private ${javaType}[] ${arrayName} = new ${javaType}[${length}];",
                            Map.of(
                                    "javaType", javaType,
                                    "arrayName", arrayName,
                                    "length", length
                            )
                    );
                }
                arrayStates.add(stateCode);
            }
        }

        List<String> allStates = new ArrayList<>(inlinedStates);
        allStates.addAll(arrayStates);
        return String.join("\n", allStates);
    }

    public String initMutableStates() {
        // It's possible that we add same mutable state twice, e.g. the `mergeExpressions` in
        // `TypedAggregateExpression`, we should call `distinct` here to remove the duplicated ones.
        List<String> initCodes = mutableStateInitCode.stream()
                .distinct()
                .map(code -> code + "\n")
                .collect(Collectors.toList());

        // The generated initialization code may exceed 64kb function size limit in JVM if there are too
        // many mutable states, so split it into multiple functions.
        return splitExpressions(initCodes, "init", Collections.emptyList());
    }

    public void addPartitionInitializationStatement(String statement) {
        partitionInitializationStatements.add(statement);
    }

    public String initPartition() {
        return String.join("\n", partitionInitializationStatements);
    }

    public void addPartitionClosureStatement(String statement) {
        partitionClosureStatements.add(statement);
    }

    public String closePartition() {
        return String.join("\n", partitionClosureStatements);
    }

    /**
     * Returns a term name that is unique within this instance of a `CodegenContext`.
     */
    public synchronized String freshName(String name) {
        String fullName = freshNamePrefix.isEmpty() ? name : freshNamePrefix + "_" + name;
        int id = freshNameIds.getOrDefault(fullName, 0);
        freshNameIds.put(fullName, id + 1);
        return CodeGeneratorUtils.template(
                "${fullName}_${id}",
                Map.of("fullName", fullName, "id", id)
        );
    }

    /**
     * Creates an `ExprValue` representing a local java variable of required data type.
     */
    public VariableValue freshVariable(String name, DataType dt) {
        return JavaCode.variable(freshName(name), dt);
    }

    /**
     * Creates an `ExprValue` representing a local java variable of required Java class.
     */
    public VariableValue freshVariable(String name, Class<?> javaClass) {
        return JavaCode.variable(freshName(name), javaClass);
    }

    /**
     * Generates code for equal expression in Java.
     */
    public String genEqual(DataType dataType, String c1, String c2) {
        if (dataType instanceof BinaryType) {
            return CodeGeneratorUtils.template(
                    "java.util.Arrays.equals(${c1}, ${c2})",
                    Map.of("c1", c1, "c2", c2)
            );
        } else if (dataType instanceof FloatType) {
            return CodeGeneratorUtils.template(
                    "((java.lang.Float.isNaN(${c1}) && java.lang.Float.isNaN(${c2})) || ${c1} == ${c2})",
                    Map.of("c1", c1, "c2", c2)
            );
        } else if (dataType instanceof DoubleType) {
            return CodeGeneratorUtils.template(
                    "((java.lang.Double.isNaN(${c1}) && java.lang.Double.isNaN(${c2})) || ${c1} == ${c2})",
                    Map.of("c1", c1, "c2", c2)
            );
        } else if (CodeGeneratorUtils.isPrimitiveType(dataType)) {
            return CodeGeneratorUtils.template(
                    "${c1} == ${c2}",
                    Map.of("c1", c1, "c2", c2)
            );
        } else if (dataType instanceof AtomicType) {
            return CodeGeneratorUtils.template(
                    "${c1}.equals(${c2})",
                    Map.of("c1", c1, "c2", c2)
            );
        } else if (dataType instanceof ArrayType) {
            return genComp(dataType, c1, c2) + " == 0";
        } else if (dataType instanceof StructType) {
            return genComp(dataType, c1, c2) + " == 0";
        } else if (dataType instanceof NullType) {
            return "false";
        } else {
            throw new RuntimeException("Cannot generate code for incomparable type: " + dataType);
        }
    }

    /**
     * Generates code for comparing two expressions.
     *
     * @param dataType data type of the expressions
     * @param c1 name of the variable of expression 1's output
     * @param c2 name of the variable of expression 2's output
     */
    public String genComp(DataType dataType, String c1, String c2) {
        if (dataType instanceof BooleanType) {
            // java boolean doesn't support > or < operator
            return CodeGeneratorUtils.template(
                    "(${c1} == ${c2} ? 0 : (${c1} ? 1 : -1))",
                    Map.of("c1", c1, "c2", c2)
            );
        } else if (dataType instanceof DoubleType) {
            String clsName = SQLOrderingUtil.class.getName().replace("$", "");
            return CodeGeneratorUtils.template(
                    "${clsName}.compareDoubles(${c1}, ${c2})",
                    Map.of("clsName", clsName, "c1", c1, "c2", c2)
            );
        } else if (dataType instanceof FloatType) {
            String clsName = SQLOrderingUtil.class.getName().replace("$", "");
            return CodeGeneratorUtils.template(
                    "${clsName}.compareFloats(${c1}, ${c2})",
                    Map.of("clsName", clsName, "c1", c1, "c2", c2)
            );
        } else if (CodeGeneratorUtils.isPrimitiveType(dataType)) {
            // use c1 - c2 may overflow
            return CodeGeneratorUtils.template(
                    "(${c1} > ${c2} ? 1 : ${c1} < ${c2} ? -1 : 0)",
                    Map.of("c1", c1, "c2", c2)
            );
        } else if (dataType instanceof BinaryType) {
            return CodeGeneratorUtils.template(
                    "com.jipple.unsafe.types.ByteArray.compareBinary(${c1}, ${c2})",
                    Map.of("c1", c1, "c2", c2)
            );
        } else if (dataType instanceof NullType) {
            return "0";
        } else if (dataType instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) dataType;
            DataType elementType = arrayType.elementType;
            String elementA = freshName("elementA");
            String isNullA = freshName("isNullA");
            String elementB = freshName("elementB");
            String isNullB = freshName("isNullB");
            String compareFunc = freshName("compareArray");
            String minLength = freshName("minLength");
            String jt = CodeGeneratorUtils.javaType(elementType);
            String funcCode = CodeGeneratorUtils.template(
                    """
                            public int ${compareFunc}(ArrayData a, ArrayData b) {
                              // when comparing unsafe arrays, try equals first as it compares the binary directly
                              // which is very fast.
                              if (a instanceof UnsafeArrayData && b instanceof UnsafeArrayData && a.equals(b)) {
                                return 0;
                              }
                              int lengthA = a.numElements();
                              int lengthB = b.numElements();
                              int ${minLength} = (lengthA > lengthB) ? lengthB : lengthA;
                              for (int i = 0; i < ${minLength}; i++) {
                                boolean ${isNullA} = a.isNullAt(i);
                                boolean ${isNullB} = b.isNullAt(i);
                                if (${isNullA} && ${isNullB}) {
                                  // Nothing
                                } else if (${isNullA}) {
                                  return -1;
                                } else if (${isNullB}) {
                                  return 1;
                                } else {
                                  ${jt} ${elementA} = ${valueA};
                                  ${jt} ${elementB} = ${valueB};
                                  int comp = ${elementComp};
                                  if (comp != 0) {
                                    return comp;
                                  }
                                }
                              }

                              if (lengthA < lengthB) {
                                return -1;
                              } else if (lengthA > lengthB) {
                                return 1;
                              }
                              return 0;
                            }
                            """,
                    Map.of(
                            "compareFunc", compareFunc,
                            "minLength", minLength,
                            "isNullA", isNullA,
                            "isNullB", isNullB,
                            "jt", jt,
                            "elementA", elementA,
                            "elementB", elementB,
                            "valueA", CodeGeneratorUtils.getValue("a", elementType, "i"),
                            "valueB", CodeGeneratorUtils.getValue("b", elementType, "i"),
                            "elementComp", genComp(elementType, elementA, elementB)
                    )
            );
            return CodeGeneratorUtils.template(
                    "${compareFunc}(${c1}, ${c2})",
                    Map.of(
                            "compareFunc", addNewFunction(compareFunc, funcCode),
                            "c1", c1,
                            "c2", c2
                    )
            );
        } else if (dataType instanceof StructType) {
            StructType schema = (StructType) dataType;
            String comparisons = GenerateOrdering.genComparisons(this, schema);
            String compareFunc = freshName("compareStruct");
            String funcCode = CodeGeneratorUtils.template(
                    """
                            public int ${compareFunc}(InternalRow a, InternalRow b) {
                              // when comparing unsafe rows, try equals first as it compares the binary directly
                              // which is very fast.
                              if (a instanceof UnsafeRow && b instanceof UnsafeRow && a.equals(b)) {
                                return 0;
                              }
                              ${comparisons}
                              return 0;
                            }
                            """,
                    Map.of(
                            "compareFunc", compareFunc,
                            "comparisons", comparisons
                    )
            );
            return CodeGeneratorUtils.template(
                    "${compareFunc}(${c1}, ${c2})",
                    Map.of(
                            "compareFunc", addNewFunction(compareFunc, funcCode),
                            "c1", c1,
                            "c2", c2
                    )
            );
        } else if (dataType instanceof AtomicType) {
            return CodeGeneratorUtils.template(
                    "${c1}.compare(${c2})",
                    Map.of("c1", c1, "c2", c2)
            );
        } else {
            throw new RuntimeException("Cannot generate code for incomparable type: " + dataType);
        }
    }

    /**
     * Generates code for greater of two expressions.
     *
     * @param dataType data type of the expressions
     * @param c1 name of the variable of expression 1's output
     * @param c2 name of the variable of expression 2's output
     */
    public String genGreater(DataType dataType, String c1, String c2) {
        String javaType = CodeGeneratorUtils.javaType(dataType);
        if (CodeGeneratorUtils.JAVA_BYTE.equals(javaType) ||
                CodeGeneratorUtils.JAVA_SHORT.equals(javaType) ||
                CodeGeneratorUtils.JAVA_INT.equals(javaType) ||
                CodeGeneratorUtils.JAVA_LONG.equals(javaType)) {
            return CodeGeneratorUtils.template(
                    "${c1} > ${c2}",
                    Map.of("c1", c1, "c2", c2)
            );
        } else {
            return CodeGeneratorUtils.template(
                    "(${comp}) > 0",
                    Map.of("comp", genComp(dataType, c1, c2))
            );
        }
    }

    /**
     * Generates code for updating `partialResult` if `item` is smaller than it.
     *
     * @param dataType data type of the expressions
     * @param partialResult {@link ExprCode} representing the partial result which has to be updated
     * @param item {@link ExprCode} representing the new expression to evaluate for the result
     */
    public String reassignIfSmaller(DataType dataType, ExprCode partialResult, ExprCode item) {
        return Block.block(
                """
                        if (!${itemIsNull} && (${partialResultIsNull} ||
                          ${genGreaterCode})) {
                          ${partialResultIsNull} = false;
                          ${partialResultValue} = ${itemValue};
                        }
                        """,
                Map.of(
                        "itemIsNull", item.isNull,
                        "partialResultIsNull", partialResult.isNull,
                        "genGreaterCode", genGreater(dataType, partialResult.value.toString(), item.value.toString()),
                        "partialResultValue", partialResult.value,
                        "itemValue", item.value
                )
        ).toString();
    }

    /**
     * Generates code for updating `partialResult` if `item` is greater than it.
     *
     * @param dataType data type of the expressions
     * @param partialResult {@link ExprCode} representing the partial result which has to be updated
     * @param item {@link ExprCode} representing the new expression to evaluate for the result
     */
    public String reassignIfGreater(DataType dataType, ExprCode partialResult, ExprCode item) {
        return Block.block(
                """
                        if (!${itemIsNull} && (${partialResultIsNull} ||
                          ${genGreaterCode})) {
                          ${partialResultIsNull} = false;
                          ${partialResultValue} = ${itemValue};
                        }
                        """,
                Map.of(
                        "itemIsNull", item.isNull,
                        "partialResultIsNull", partialResult.isNull,
                        "genGreaterCode", genGreater(dataType, item.value.toString(), partialResult.value.toString()),
                        "partialResultValue", partialResult.value,
                        "itemValue", item.value
                )
        ).toString();
    }

    /**
     * Generates code to do null safe execution, i.e. only execute the code when the input is not
     * null by adding null check if necessary.
     *
     * @param nullable used to decide whether we should add null check or not.
     * @param isNull the code to check if the input is null.
     * @param execute the code that should only be executed when the input is not null.
     */
    public String nullSafeExec(boolean nullable, String isNull, String execute) {
/*
    if (nullable) {
      s"""
        if (!$isNull) {
          $execute
        }
      """
    } else {
      "\n" + execute
    }
* */
        if (nullable) {
            return CodeGeneratorUtils.template("\n" + """
                if (!${isNull}) {
                  ${execute}
                }
            """, Map.of("isNull", isNull, "execute", execute));
        } else {
            return "\n" + execute;
        }
    }

    /**
     * Generates code to do null safe execution when accessing properties of complex
     * ArrayData elements.
     *
     * @param nullElements used to decide whether the ArrayData might contain null or not.
     * @param isNull a variable indicating whether the result will be evaluated to null or not.
     * @param arrayData a variable name representing the ArrayData.
     * @param execute the code that should be executed only if the ArrayData doesn't contain
     *                any null.
     */
    public String nullArrayElementsSaveExec(
            boolean nullElements,
            String isNull,
            String arrayData,
            String execute) {
        String i = freshName("idx");
        if (nullElements) {
            return Block.block(
                    """
                            for (int ${i} = 0; !${isNull} && ${i} < ${arrayData}.numElements(); ${i}++) {
                              ${isNull} |= ${arrayData}.isNullAt(${i});
                            }
                            if (!${isNull}) {
                              ${execute}
                            }
                            """,
                    Map.of(
                            "i", i,
                            "isNull", isNull,
                            "arrayData", arrayData,
                            "execute", execute
                    )
            ).toString();
        } else {
            return execute;
        }
    }

    /**
     * Splits the generated code of expressions into multiple functions, because function has
     * 64kb code size limit in JVM. If the class to which the function would be inlined would grow
     * beyond 1000kb, we declare a private, inner sub-class, and the function is inlined to it
     * instead, because classes have a constant pool limit of 65,536 named values.
     *
     * @param expressions the codes to evaluate expressions.
     * @param funcName the split function name base.
     * @param arguments the list of (type, name) of the arguments of the split function.
     */
    public String splitExpressions(
            List<String> expressions,
            String funcName,
            List<Tuple2<String, String>> arguments) {
        return splitExpressions(
                expressions,
                funcName,
                arguments,
                "void",
                Function.identity(),
                CodegenContext::defaultFoldFunctions);
    }

    /**
     * Splits the generated code of expressions into multiple functions, because function has
     * 64kb code size limit in JVM. If the class to which the function would be inlined would grow
     * beyond 1000kb, we declare a private, inner sub-class, and the function is inlined to it
     * instead, because classes have a constant pool limit of 65,536 named values.
     *
     * @param expressions the codes to evaluate expressions.
     * @param funcName the split function name base.
     * @param arguments the list of (type, name) of the arguments of the split function.
     * @param returnType the return type of the split function.
     * @param makeSplitFunction makes split function body, e.g. add preparation or cleanup.
     * @param foldFunctions folds the split function calls.
     */
    public String splitExpressions(
            List<String> expressions,
            String funcName,
            List<Tuple2<String, String>> arguments,
            String returnType,
            Function<String, String> makeSplitFunction,
            Function<List<String>, String> foldFunctions) {
        List<String> blocks = buildCodeBlocks(expressions);

        if (blocks.size() == 1) {
            // inline execution if only one block
            return blocks.get(0);
        } else {
            if (Utils.isTesting()) {
                for (Tuple2<String, String> arg : arguments) {
                    assertCondition(!mutableStateNames.contains(arg._2), () -> "split function argument " + arg._2 + " cannot be a global variable.");
                }
            }

            String func = freshName(funcName);
            String argString = arguments.stream()
                    .map(arg -> arg._1 + " " + arg._2)
                    .collect(Collectors.joining(", "));

            List<NewFunctionSpec> functions = new ArrayList<>();
            for (int i = 0; i < blocks.size(); i++) {
                String body = blocks.get(i);
                String name = func + "_" + i;
                String code = CodeGeneratorUtils.template(
                        """
                                private ${returnType} ${name}(${argString}) {
                                  ${body}
                                }
                                """,
                        Map.of(
                                "returnType", returnType,
                                "name", name,
                                "argString", argString,
                                "body", makeSplitFunction.apply(body)
                        )
                );
                functions.add(addNewFunctionInternal(name, code, false));
            }

            List<NewFunctionSpec> outerClassFunctions = new ArrayList<>();
            List<NewFunctionSpec> innerClassFunctions = new ArrayList<>();
            for (NewFunctionSpec fn : functions) {
                if (fn.innerClassName == null) {
                    outerClassFunctions.add(fn);
                } else {
                    innerClassFunctions.add(fn);
                }
            }

            String argsString = arguments.stream()
                    .map(arg -> arg._2)
                    .collect(Collectors.joining(", "));
            List<String> outerClassFunctionCalls = outerClassFunctions.stream()
                    .map(f -> f.functionName + "(" + argsString + ")")
                    .collect(Collectors.toList());

            List<String> innerClassFunctionCalls = generateInnerClassesFunctionCalls(
                    innerClassFunctions,
                    func,
                    arguments,
                    returnType,
                    makeSplitFunction,
                    foldFunctions);

            List<String> allCalls = new ArrayList<>(outerClassFunctionCalls);
            allCalls.addAll(innerClassFunctionCalls);
            return foldFunctions.apply(allCalls);
        }
    }

    /**
     * Splits the generated code of expressions into multiple functions with current inputs.
     * TODO: support whole stage codegen
     *
     * Note that different from {@link #splitExpressions}, we will extract the current inputs of this
     * context and pass them to the generated functions. The input is {@code INPUT_ROW} for normal
     * codegen path, and {@code currentVars} for whole stage codegen path. Whole stage codegen path is
     * not supported yet.
     *
     * @param expressions the codes to evaluate expressions.
     * @param funcName the split function name base.
     * @param extraArguments the list of (type, name) of the arguments of the split function,
     *                       except for the current inputs like {@code INPUT_ROW}.
     * @param returnType the return type of the split function.
     * @param makeSplitFunction makes split function body, e.g. add preparation or cleanup.
     * @param foldFunctions folds the split function calls.
     */
    public String splitExpressionsWithCurrentInputs(
            List<String> expressions,
            String funcName,
            List<Tuple2<String, String>> extraArguments,
            String returnType,
            Function<String, String> makeSplitFunction,
            Function<List<String>, String> foldFunctions) {
        if (INPUT_ROW == null || currentVars != null) {
            return String.join("\n", expressions);
        } else {
            List<Tuple2<String, String>> arguments = new ArrayList<>();
            arguments.add(Tuple2.of("InternalRow", INPUT_ROW));
            arguments.addAll(extraArguments);
            return splitExpressions(
                    expressions,
                    funcName,
                    arguments,
                    returnType,
                    makeSplitFunction,
                    foldFunctions);
        }
    }

    public String splitExpressionsWithCurrentInputs(List<String> expressions) {
        return splitExpressionsWithCurrentInputs(expressions, "apply", Collections.emptyList());
    }

    public String splitExpressionsWithCurrentInputs(
            List<String> expressions,
            String funcName,
            List<Tuple2<String, String>> extraArguments) {
        return splitExpressionsWithCurrentInputs(
                expressions,
                funcName,
                extraArguments,
                "void",
                Function.identity(),
                CodegenContext::defaultFoldFunctions);
    }

    /**
     * Returns the code for subexpression elimination after splitting it if necessary.
     */
    public String subexprFunctionsCode() {
        // Whole-stage codegen's subexpression elimination is handled in another code path
        assertCondition(currentVars == null || subexprFunctions.isEmpty());
        List<Tuple2<String, String>> args = Collections.singletonList(
                Tuple2.of("InternalRow", INPUT_ROW));
        return splitExpressions(subexprFunctions, "subexprFunc_split", args);
    }

    /**
     * Generates code for expressions. If doSubexpressionElimination is true, subexpression
     * elimination will be performed. Subexpression elimination assumes that the code for each
     * expression will be combined in the {@code expressions} order.
     */
    public List<ExprCode> generateExpressions(
            List<Expression> expressions,
            boolean doSubexpressionElimination) {
        // We need to make sure that we do not reuse stateful expressions.
        List<Expression> cleanedExpressions = new ArrayList<>();
        for (Expression e : expressions) {
            cleanedExpressions.add(e.freshCopyIfContainsStatefulExpression());
        }
        if (doSubexpressionElimination) {
            subexpressionElimination(cleanedExpressions);
        }
        List<ExprCode> result = new ArrayList<>();
        for (Expression e : cleanedExpressions) {
            result.add(e.genCode(this));
        }
        return result;
    }

    public List<ExprCode> generateExpressions(List<Expression> expressions) {
        return generateExpressions(expressions, false);
    }

    /**
     * get a map of the pair of a place holder and a corresponding comment
     */
    public Map<String, String> getPlaceHolderToComments() {
        return new HashMap<>(placeHolderToComments);
    }

    /**
     * Register a comment and return the corresponding place holder
     *
     * @param placeholderId an optionally specified identifier for the comment's placeholder.
     *                      The caller should make sure this identifier is unique within the
     *                      compilation unit. If this argument is not specified, a fresh identifier
     *                      will be automatically created and used as the placeholder.
     * @param force whether to force registering the comments
     */
    public Block registerComment(String text, String placeholderId, boolean force) {
        if (force || false /*SQLConf.get.codegenComments*/) {
            String name;
            if (!placeholderId.isEmpty()) {
                assertCondition(!placeHolderToComments.containsKey(placeholderId),
                        "Comment placeholder already exists: " + placeholderId);
                name = placeholderId;
            } else {
                name = freshName("c");
            }

            String comment;
            if (text.contains("\n") || text.contains("\r")) {
                String[] lines = text.split("(\r\n)|\r|\n");
                comment = "/**\n * " + String.join("\n * ", lines) + "\n */";
            } else {
                comment = "// " + text;
            }
            placeHolderToComments.put(name, comment);
            return Block.block("/*${name}*/", Map.of("name", name));
        } else {
            return EmptyBlock.INSTANCE;
        }
    }

    public Block registerComment(String text) {
        return registerComment(text, "", false);
    }

    // 以下方法暂时简化实现或留空，后续可以逐步完善

    /**
     * Adds a function to the generated class.
     *
     * If the code for the {@code OuterClass} grows too large, the function will be inlined into a
     * new private, inner class, and a class-qualified name for the function will be returned.
     * Otherwise, the function will be inlined to the {@code OuterClass} and the simple
     * {@code funcName} will be returned.
     *
     * @param funcName the class-unqualified name of the function
     * @param funcCode the body of the function
     * @param inlineToOuterClass whether the given code must be inlined to the {@code OuterClass}.
     *                           This can be necessary when a function is declared outside of the context
     *                           it is eventually referenced and a returned qualified function name
     *                           cannot otherwise be accessed.
     * @return the name of the function, qualified by class if it will be inlined to a private,
     *         inner class
     */
    public String addNewFunction(String funcName, String funcCode, boolean inlineToOuterClass) {
        NewFunctionSpec newFunction = addNewFunctionInternal(funcName, funcCode, inlineToOuterClass);
        if (newFunction.innerClassName == null && newFunction.innerClassInstance == null) {
            return newFunction.functionName;
        } else if (newFunction.innerClassName != null && newFunction.innerClassInstance != null) {
            return newFunction.innerClassInstance + "." + newFunction.functionName;
        } else {
            throw new IllegalStateException("addNewFunction mismatched for " + funcName);
        }
    }

    public String addNewFunction(String funcName, String funcCode) {
        return addNewFunction(funcName, funcCode, false);
    }

    /**
     * Declares all function code. If the added functions are too many, split them into nested
     * sub-classes to avoid hitting Java compiler constant pool limitation.
     *
     * Nested, private sub-classes have no mutable state (though they do reference the outer class'
     * mutable state), so we declare and initialize them inline to the OuterClass.
     */
    public String declareAddedFunctions() {
        Collection<String> inlinedFunctions = classFunctions.get(outerClassName).values();

        List<String> initNestedClasses = new ArrayList<>();
        for (Tuple2<String, String> classInfo : classes) {
            if (!outerClassName.equals(classInfo._1)) {
                initNestedClasses.add(CodeGeneratorUtils.template(
                        "private ${className} ${classInstance} = new ${className}();",
                        Map.of(
                                "className", classInfo._1,
                                "classInstance", classInfo._2
                        )
                ));
            }
        }

        List<String> declareNestedClasses = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : classFunctions.entrySet()) {
            String className = entry.getKey();
            if (!outerClassName.equals(className)) {
                String functions = String.join("\n", entry.getValue().values());
                String code = CodeGeneratorUtils.template(
                        """
                                private class ${className} {
                                  ${functions}
                                }
                                """,
                        Map.of(
                                "className", className,
                                "functions", functions
                        )
                );
                declareNestedClasses.add(code);
            }
        }

        List<String> all = new ArrayList<>();
        all.addAll(inlinedFunctions);
        all.addAll(initNestedClasses);
        all.addAll(declareNestedClasses);
        return String.join("\n", all);
    }

    /**
     * Emits extra inner classes added with addExtraCode
     */
    public String emitExtraCode() {
        return String.join("\n", extraClasses);
    }

    /**
     * Add extra source code to the outermost generated class.
     */
    public void addInnerClass(String code) {
        extraClasses.add(code);
    }

    /**
     * Returns the size of the most recently added class.
     */
    private int currClassSize() {
        return classSize.getOrDefault(classes.get(0)._1, 0);
    }

    /**
     * Returns the class name and instance name for the most recently added class.
     */
    private Tuple2<String, String> currClass() {
        return classes.get(0);
    }

    /**
     * Adds a new class. Requires the class' name, and its instance name.
     */
    private void addClass(String className, String classInstance) {
        classes.add(0, Tuple2.of(className, classInstance));
        classSize.put(className, 0);
        classFunctions.put(className, new HashMap<>());
    }

    /**
     * Metadata describing a newly added function and its containing class (if any).
     */
    private static final class NewFunctionSpec {
        private final String functionName;
        private final String innerClassName;
        private final String innerClassInstance;

        private NewFunctionSpec(String functionName, String innerClassName, String innerClassInstance) {
            this.functionName = functionName;
            this.innerClassName = innerClassName;
            this.innerClassInstance = innerClassInstance;
        }
    }

    /**
     * Adds a function to either the current class or a new nested class if size limits are exceeded.
     */
    private NewFunctionSpec addNewFunctionInternal(
            String funcName,
            String funcCode,
            boolean inlineToOuterClass) {
        String className;
        String classInstance = null;
        if (inlineToOuterClass) {
            className = outerClassName;
        } else if (currClassSize() > CodeGeneratorUtils.GENERATED_CLASS_SIZE_THRESHOLD) {
            className = freshName("NestedClass");
            classInstance = freshName("nestedClassInstance");
            addClass(className, classInstance);
        } else {
            Tuple2<String, String> curr = currClass();
            className = curr._1;
            classInstance = curr._2;
        }

        addNewFunctionToClass(funcName, funcCode, className);

        if (outerClassName.equals(className)) {
            return new NewFunctionSpec(funcName, null, null);
        }
        return new NewFunctionSpec(funcName, className, classInstance);
    }

    /**
     * Adds a function definition to the specified class and updates its size.
     */
    private void addNewFunctionToClass(String funcName, String funcCode, String className) {
        classSize.put(className, classSize.getOrDefault(className, 0) + funcCode.length());
        classFunctions.get(className).put(funcName, funcCode);
    }

    /**
     * Splits expression code into blocks based on a length threshold.
     */
    private List<String> buildCodeBlocks(List<String> expressions) {
        List<String> blocks = new ArrayList<>();
        StringBuilder blockBuilder = new StringBuilder();
        int length = 0;
        int splitThreshold = SQLConf.get().methodSplitThreshold();
        for (String code : expressions) {
            if (length > splitThreshold) {
                blocks.add(blockBuilder.toString());
                blockBuilder.setLength(0);
                length = 0;
            }
            blockBuilder.append(code);
            length += CodeFormatter.stripExtraNewLinesAndComments(code).length();
        }
        blocks.add(blockBuilder.toString());
        return blocks;
    }

    /**
     * Groups inner-class functions to reduce constant pool pressure and generates call sites.
     */
    private List<String> generateInnerClassesFunctionCalls(
            List<NewFunctionSpec> functions,
            String funcName,
            List<Tuple2<String, String>> arguments,
            String returnType,
            Function<String, String> makeSplitFunction,
            Function<List<String>, String> foldFunctions) {
        Map<Tuple2<String, String>, List<String>> innerClassToFunctions = new LinkedHashMap<>();
        for (NewFunctionSpec function : functions) {
            Tuple2<String, String> key = Tuple2.of(function.innerClassName, function.innerClassInstance);
            List<String> list = innerClassToFunctions.getOrDefault(key, new ArrayList<>());
            list.add(0, function.functionName);
            innerClassToFunctions.put(key, list);
        }

        String argDefinitionString = arguments.stream()
                .map(arg -> arg._1 + " " + arg._2)
                .collect(Collectors.joining(", "));
        String argInvocationString = arguments.stream()
                .map(arg -> arg._2)
                .collect(Collectors.joining(", "));

        List<String> calls = new ArrayList<>();
        for (Map.Entry<Tuple2<String, String>, List<String>> entry : innerClassToFunctions.entrySet()) {
            String innerClassName = entry.getKey()._1;
            String innerClassInstance = entry.getKey()._2;
            List<String> orderedFunctions = new ArrayList<>(entry.getValue());
            Collections.reverse(orderedFunctions);

            if (orderedFunctions.size() > CodeGeneratorUtils.MERGE_SPLIT_METHODS_THRESHOLD) {
                List<String> functionCalls = orderedFunctions.stream()
                        .map(name -> name + "(" + argInvocationString + ")")
                        .collect(Collectors.toList());
                String body = foldFunctions.apply(functionCalls);
                String code = CodeGeneratorUtils.template(
                        """
                                private ${returnType} ${funcName}(${argDefinitionString}) {
                                  ${body}
                                }
                                """,
                        Map.of(
                                "returnType", returnType,
                                "funcName", funcName,
                                "argDefinitionString", argDefinitionString,
                                "body", makeSplitFunction.apply(body)
                        )
                );
                addNewFunctionToClass(funcName, code, innerClassName);
                calls.add(innerClassInstance + "." + funcName + "(" + argInvocationString + ")");
            } else {
                for (String f : orderedFunctions) {
                    calls.add(innerClassInstance + "." + f + "(" + argInvocationString + ")");
                }
            }
        }
        return calls;
    }

    /**
     * Builds subexpression elimination functions and registers the generated states.
     */
    private void subexpressionElimination(List<Expression> expressions) {
        for (Expression expression : expressions) {
            equivalentExpressions.addExprTree(expression);
        }

        List<Expression> commonExprs = equivalentExpressions.getCommonSubexpressions();
        for (Expression expr : commonExprs) {
            String fnName = freshName("subExpr");
            String isNull = addMutableState(CodeGeneratorUtils.JAVA_BOOLEAN, "subExprIsNull");
            String value = addMutableState(CodeGeneratorUtils.javaType(expr.dataType()), "subExprValue");

            ExprCode eval = expr.genCode(this);
            String fn = CodeGeneratorUtils.template(
                    """
                            private void ${fnName}(InternalRow ${inputRow}) {
                              ${evalCode}
                              ${isNull} = ${evalIsNull};
                              ${value} = ${evalValue};
                            }
                            """,
                    Map.of(
                            "fnName", fnName,
                            "inputRow", INPUT_ROW,
                            "evalCode", eval.code.toString(),
                            "isNull", isNull,
                            "evalIsNull", eval.isNull.toString(),
                            "value", value,
                            "evalValue", eval.value.toString()
                    )
            );

            String subExprCode = CodeGeneratorUtils.template(
                    "${fnName}(${inputRow});",
                    Map.of("fnName", addNewFunction(fnName, fn), "inputRow", INPUT_ROW)
            );
            subexprFunctions.add(subExprCode);

            ExprCode stateCode = new ExprCode(
                    Block.block("${subExprCode}", Map.of("subExprCode", subExprCode)),
                    JavaCode.isNullGlobal(isNull),
                    JavaCode.global(value, expr.dataType()));
            SubExprEliminationState state = SubExprEliminationState.apply(stateCode);
            subExprEliminationExprs.put(new ExpressionEquals(expr), state);
        }
    }

    /**
     * Default fold behavior for split-function calls: join with semicolons.
     */
    private static String defaultFoldFunctions(List<String> functions) {
        if (functions.isEmpty()) {
            return "";
        }
        return String.join(";\n", functions) + ";";
    }

}

