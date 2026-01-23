package com.jipple.sql.catalyst.expressions.codegen;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.EquivalentExpressions;
import com.jipple.sql.catalyst.expressions.ExpressionEquals;
import com.jipple.sql.catalyst.util.SQLOrderingUtil;
import com.jipple.sql.types.*;
import com.jipple.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A context for codegen, tracking a list of objects that could be passed into generated Java
 * function.
 */
public class CodegenContext {
    private static final Logger logger = LoggerFactory.getLogger(CodegenContext.class);

    /**
     * Holding a list of objects that could be used passed into generated class.
     */
    private final List<Object> references = new ArrayList<>();

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
     * Add a mutable state as a field to the generated class.
     *
     * @param javaType Java type of the field.
     * @param variableName Name of the field.
     * @param initFunc Function includes statement(s) to put into the init() method to initialize
     *                 this field. The argument is the name of the mutable state variable.
     *                 If left blank, the field will be default-initialized.
     * @param forceInline whether the declaration and initialization code may be inlined rather than
     *                    compacted.
     * @param useFreshName If this is false and the mutable state ends up inlining in the outer
     *                     class, the name is not changed
     * @return the name of the mutable state variable
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
            MutableStateArrays arrays = arrayCompactedMutableStates.computeIfAbsent(
                    javaType, k -> new MutableStateArrays());
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
     * with that name.
     */
    public void addImmutableStateIfNotExists(
            String javaType,
            String variableName,
            Function<String, String> initFunc) {
        Tuple2<String, String> existingImmutableState = immutableStates.get(variableName);
        if (existingImmutableState == null) {
            addMutableState(javaType, variableName, initFunc, false, true);
            immutableStates.put(variableName, Tuple2.of(javaType, initFunc.apply(variableName)));
        } else {
            String prevJavaType = existingImmutableState._1;
            String prevInitCode = existingImmutableState._2;
            if (!prevJavaType.equals(javaType)) {
                throw new AssertionError(variableName + " has already been defined with type " +
                        prevJavaType + " and now it is tried to define again with type " + javaType + ".");
            }
            if (!prevInitCode.equals(initFunc.apply(variableName))) {
                throw new AssertionError(variableName + " has already been defined " +
                        "with different initialization statements.");
            }
        }
    }

    public void addImmutableStateIfNotExists(String javaType, String variableName) {
        addImmutableStateIfNotExists(javaType, variableName, s -> "");
    }

    /**
     * Add buffer variable which stores data coming from an [[InternalRow]].
     */
    public ExprCode addBufferedState(DataType dataType, String variableName, String initCode) {
        String value = addMutableState(CodeGeneratorUtils.javaType(dataType), variableName);
        Block code;
        // TODO: 实现 UserDefinedType.sqlType 方法
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
            // TODO: 实现数组比较的复杂逻辑
            return "0"; // 简化实现
        } else if (dataType instanceof StructType) {
            // TODO: 实现结构体比较的复杂逻辑
            return "0"; // 简化实现
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
     * Generates code to do null safe execution.
     */
    public String nullSafeExec(boolean nullable, String isNull, String execute) {
        if (nullable) {
            return Block.block(
                    """
                            if (!${isNull}) {
                              ${execute}
                            }
                            """,
                    Map.of("isNull", isNull, "execute", execute)
            ).toString();
        } else {
            return "\n" + execute;
        }
    }

    /**
     * Splits the generated code of expressions into multiple functions.
     * TODO: 完整实现这个方法
     */
    public String splitExpressions(
            List<String> expressions,
            String funcName,
            List<Tuple2<String, String>> arguments) {
        // 简化实现：直接返回所有表达式连接
        return String.join("\n", expressions);
    }

    /**
     * Returns the code for subexpression elimination after splitting it if necessary.
     */
    public String subexprFunctionsCode() {
        // Whole-stage codegen's subexpression elimination is handled in another code path
        assert currentVars == null || subexprFunctions.isEmpty();
        List<Tuple2<String, String>> args = Collections.singletonList(
                Tuple2.of("InternalRow", INPUT_ROW));
        return splitExpressions(subexprFunctions, "subexprFunc_split", args);
    }

    /**
     * Generates code for expressions.
     */
    public List<ExprCode> generateExpressions(
            List<Expression> expressions,
            boolean doSubexpressionElimination) {
        // We need to make sure that we do not reuse stateful expressions.
        List<Expression> cleanedExpressions = new ArrayList<>();
        for (Expression e : expressions) {
            // TODO: 实现 freshCopyIfContainsStatefulExpression 方法
            cleanedExpressions.add(e); // e.freshCopyIfContainsStatefulExpression()
        }
        if (doSubexpressionElimination) {
            // TODO: 实现 subexpressionElimination
        }
        List<ExprCode> result = new ArrayList<>();
        for (Expression e : cleanedExpressions) {
            // TODO: 实现 genCode 方法
            // result.add(e.genCode(this));
            result.add(ExprCode.forNullValue(e.dataType())); // 临时实现
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
     */
    public Block registerComment(String text, String placeholderId, boolean force) {
        // TODO: 完整实现
        return EmptyBlock.INSTANCE;
    }

    public Block registerComment(String text) {
        return registerComment(text, "", false);
    }

    // 以下方法暂时简化实现或留空，后续可以逐步完善

    /**
     * Adds a function to the generated class.
     * TODO: 完整实现
     */
    public String addNewFunction(String funcName, String funcCode, boolean inlineToOuterClass) {
        // 简化实现
        return funcName;
    }

    public String addNewFunction(String funcName, String funcCode) {
        return addNewFunction(funcName, funcCode, false);
    }

    /**
     * Declares all function code.
     * TODO: 完整实现
     */
    public String declareAddedFunctions() {
        return "";
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
}

