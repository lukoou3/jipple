package com.jipple.sql.catalyst.expressions.predicate;

import com.google.common.base.Preconditions;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.UnaryExpression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.TypeUtils;
import com.jipple.sql.types.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

/**
 * Optimized version of In clause, when all filter values of In clause are static.
 */
public class InSet extends UnaryExpression {
    public final Set<Object> hset;
    private transient Set<Object> set;
    private transient Boolean hasNull;
    private transient Boolean hasNaN;

    public InSet(Expression child, Set<Object> hset) {
        super(child);
        Preconditions.checkArgument(hset != null, "hset could not be null");
        this.hset = hset;
    }

    @Override
    public Object[] args() {
        return new Object[]{child, hset};
    }

    @Override
    public String toString() {
        String listString = hset.stream()
                .map(elem -> new Literal(elem, child.dataType()).toString())
                .sorted()
                .collect(Collectors.joining(", "));
        return child + " INSET " + listString;
    }

    @Override
    public boolean nullable() {
        return child.nullable() || hasNull();
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    @Override
    public Object eval(InternalRow input) {
        if (hset.isEmpty()) {
            return false;
        } else {
            Object value = child.eval(input);
            if (value == null) {
                return null;
            } else if (set().contains(value)) {
                return true;
            } else if (isNaNValue(value)) {
                return hasNaN();
            } else if (hasNull()) {
                return null;
            } else {
                return false;
            }
        }
    }

    private boolean hasNull() {
        if (hasNull == null) {
            hasNull = hset.contains(null);
        }
        return hasNull;
    }

    private boolean hasNaN() {
        if (hasNaN == null) {
            if (child.dataType() instanceof DoubleType || child.dataType() instanceof FloatType) {
                hasNaN = hset.stream().anyMatch(this::isNaNValue);
            } else {
                hasNaN = false;
            }
        }
        return hasNaN;
    }

    private boolean isNaNValue(Object value) {
        if (child.dataType() instanceof DoubleType) {
            return value instanceof Double d && Double.isNaN(d);
        }
        if (child.dataType() instanceof FloatType) {
            return value instanceof Float f && Float.isNaN(f);
        }
        return false;
    }

    private Set<Object> set() {
        if (set == null) {
            DataType dt = child.dataType();
            if (dt instanceof AtomicType && !(dt instanceof BinaryType)) {
                set = hset;
            } else if (dt instanceof NullType) {
                set = hset;
            } else {
                Comparator<Object> comparator = TypeUtils.getInterpretedComparator(dt);
                TreeSet<Object> treeSet = new TreeSet<>(comparator);
                for (Object value : hset) {
                    if (value != null) {
                        treeSet.add(value);
                    }
                }
                set = treeSet;
            }
        }
        return set;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        if (hset.isEmpty()) {
            return ev.copy(Block.block(
                    """
                            ${javaBoolean} ${value} = false;
                            ${javaBoolean} ${isNull} = false;
                            """,
                    Map.of(
                            "javaBoolean", CodeGeneratorUtils.JAVA_BOOLEAN,
                            "value", ev.value,
                            "isNull", ev.isNull
                    )
            ));
        } else if (canBeComputedUsingSwitch() && hset.size() <= 400) {
            return genCodeWithSwitch(ctx, ev);
        } else {
            return genCodeWithSet(ctx, ev);
        }
    }

    private boolean canBeComputedUsingSwitch() {
        return child.dataType() instanceof IntegerType || child.dataType() instanceof DateType;
    }

    private ExprCode genCodeWithSet(CodegenContext ctx, ExprCode ev) {
        return nullSafeCodeGen(ctx, ev, c -> {
            String setTerm = ctx.addReferenceObj("set", set());
            String setIsNull = hasNull() ? CodeGeneratorUtils.template(
                    "${isNull} = !${value};",
                    Map.of("isNull", ev.isNull, "value", ev.value)
            ) : "";
            String isNaNCode = child.dataType() instanceof DoubleType
                    ? "java.lang.Double.isNaN(" + c + ")"
                    : (child.dataType() instanceof FloatType
                    ? "java.lang.Float.isNaN(" + c + ")"
                    : null);
            if (hasNaN() && isNaNCode != null) {
                return CodeGeneratorUtils.template(
                        """
                                if (${setTerm}.contains(${valueArg})) {
                                  ${value} = true;
                                } else if (${isNaNCode}) {
                                  ${value} = true;
                                }
                                ${setIsNull}
                                """,
                        Map.ofEntries(
                                Map.entry("setTerm", setTerm),
                                Map.entry("valueArg", c),
                                Map.entry("value", ev.value),
                                Map.entry("isNaNCode", isNaNCode),
                                Map.entry("setIsNull", setIsNull)
                        )
                );
            } else {
                return CodeGeneratorUtils.template(
                        """
                                ${value} = ${setTerm}.contains(${valueArg});
                                ${setIsNull}
                                """,
                        Map.ofEntries(
                                Map.entry("value", ev.value),
                                Map.entry("setTerm", setTerm),
                                Map.entry("valueArg", c),
                                Map.entry("setIsNull", setIsNull)
                        )
                );
            }
        });
    }

    private ExprCode genCodeWithSwitch(CodegenContext ctx, ExprCode ev) {
        List<ExprCode> caseValuesGen = hset.stream()
                .filter(v -> v != null)
                .map(v -> new Literal(v, child.dataType()).genCode(ctx))
                .toList();
        ExprCode valueGen = child.genCode(ctx);

        String caseBranches = caseValuesGen.stream()
                .map(literal -> CodeGeneratorUtils.template(
                        """
                                case ${literalValue}:
                                  ${value} = true;
                                  break;
                                """,
                        Map.of(
                                "literalValue", literal.value,
                                "value", ev.value
                        )
                ))
                .collect(Collectors.joining("\n"));

        String switchCode = caseBranches.isEmpty()
                ? CodeGeneratorUtils.template(
                "${isNull} = ${hasNull};",
                Map.of("isNull", ev.isNull, "hasNull", String.valueOf(hasNull()))
        )
                : CodeGeneratorUtils.template(
                """
                        switch (${value}) {
                          ${caseBranches}
                          default:
                            ${isNull} = ${hasNull};
                        }
                        """,
                Map.ofEntries(
                        Map.entry("value", valueGen.value),
                        Map.entry("caseBranches", caseBranches),
                        Map.entry("isNull", ev.isNull),
                        Map.entry("hasNull", String.valueOf(hasNull()))
                )
        );

        return ev.copy(Block.block(
                """
                        ${valueCode}
                        ${javaBoolean} ${isNull} = ${valueIsNull};
                        ${javaBoolean} ${value} = false;
                        if (!${valueIsNull}) {
                          ${switchCode}
                        }
                        """,
                Map.ofEntries(
                        Map.entry("valueCode", valueGen.code),
                        Map.entry("javaBoolean", CodeGeneratorUtils.JAVA_BOOLEAN),
                        Map.entry("isNull", ev.isNull),
                        Map.entry("valueIsNull", valueGen.isNull),
                        Map.entry("value", ev.value),
                        Map.entry("switchCode", switchCode)
                )
        ));
    }

    @Override
    public String sql() {
        String valueSQL = child.sql();
        String listSQL = hset.stream()
                .map(elem -> new Literal(elem, child.dataType()).sql())
                .sorted()
                .collect(Collectors.joining(", "));
        return "(" + valueSQL + " IN (" + listSQL + "))";
    }

    @Override
    public Expression withNewChildInternal(Expression newChild) {
        return new InSet(newChild, hset);
    }
}
