package com.jipple.sql.catalyst.expressions.predicate;

import com.google.common.base.Preconditions;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.util.TypeUtils;
import com.jipple.sql.types.AtomicType;
import com.jipple.sql.types.DataType;
import com.jipple.tuple.Tuple2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class In extends Expression {
    public final Expression value;
    public final List<Expression> list;
    Comparator<Object> _comparator;

    public In(Expression value, List<Expression> list) {
        Preconditions.checkArgument(list != null, "list should not be null");
        this.value = value;
        this.list = list;
    }

    @Override
    public Object[] args() {
        return new Object[] {value, list};
    }

    @Override
    public List<Expression> children() {
        List<Expression> children = new ArrayList<>(list.size() + 1);
        children.add(value);
        children.addAll(list);
        return children;
    }

    @Override
    public boolean foldable() {
        return children().stream().allMatch(x -> x.foldable());
    }

    @Override
    public boolean nullable() {
        return children().stream().anyMatch(x -> x.nullable());
    }

    @Override
    public DataType dataType() {
        return BOOLEAN;
    }

    public boolean inSetConvertible() {
        return list.stream().allMatch(x -> x instanceof Literal);
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        boolean mismatchOpt = list.stream().anyMatch(l -> !DataType.equalsStructurally(l.dataType(), value.dataType(),  true));
        if (mismatchOpt) {
            return TypeCheckResult.dataTypeMismatch("DATA_DIFF_TYPES",
                    Map.of("functionName", prettyName(), "dataType", children().stream().map(child -> child.dataType().simpleString()).collect(Collectors.joining("[", ", ", "]"))));
        }
        if(!(value.dataType() instanceof AtomicType)){
            return TypeCheckResult.typeCheckFailure("not support order type:" + value.dataType());
        }
        return TypeCheckResult.typeCheckSuccess();
    }

    @Override
    public String toString() {
        return value + " IN " + list.stream().map(Expression::toString).collect(Collectors.joining(",", "(", ")"));
    }

    private Comparator<Object> comparator(){
        if(_comparator == null){
            _comparator = TypeUtils.getInterpretedComparator(value.dataType());
        }
        return _comparator;
    }

    @Override
    public Object eval(InternalRow input) {
        if (list.isEmpty()) {
            return false;
        } else {
            Object evaluatedValue = value.eval(input);
            if (evaluatedValue == null) {
                return null;
            } else {
                boolean hasNull = false;
                Comparator<Object> comparator = comparator();
                for (Expression e : list) {
                    Object v = e.eval(input);
                    if (v == null) {
                        hasNull = true;
                    } else if (comparator.compare(v, evaluatedValue) == 0) {
                        return true;
                    }
                }
                return hasNull ? null : false;
            }
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        if (list.isEmpty()) {
            return ev.copy(Block.block(
                    """
                            final boolean ${isNull} = false;
                            final boolean ${value} = false;
                            """,
                    Map.of(
                            "isNull", ev.isNull,
                            "value", ev.value
                    )
            ));
        } else {
            String javaDataType = CodeGeneratorUtils.javaType(value.dataType());
            ExprCode valueGen = value.genCode(ctx);
            List<ExprCode> listGen = list.stream().map(expr -> expr.genCode(ctx)).toList();
            int HAS_NULL = -1;
            int NOT_MATCHED = 0;
            int MATCHED = 1;
            String tmpResult = ctx.freshName("inTmpResult");
            String valueArg = ctx.freshName("valueArg");

            List<String> listCode = new ArrayList<>(listGen.size());
            for (ExprCode x : listGen) {
                String equalsCode = ctx.genEqual(value.dataType(), valueArg, x.value.toString());
                String code = CodeGeneratorUtils.template(
                        """
                                ${xCode}
                                if (${xIsNull}) {
                                  ${tmpResult} = ${hasNull}; // ${isNull} = true;
                                } else if (${equals}) {
                                  ${tmpResult} = ${matched}; // ${isNull} = false; ${value} = true;
                                  continue;
                                }
                                """,
                        Map.ofEntries(
                                Map.entry("xCode", x.code),
                                Map.entry("xIsNull", x.isNull),
                                Map.entry("tmpResult", tmpResult),
                                Map.entry("hasNull", HAS_NULL),
                                Map.entry("equals", equalsCode),
                                Map.entry("matched", MATCHED),
                                Map.entry("isNull", ev.isNull),
                                Map.entry("value", ev.value)
                        )
                );
                listCode.add(code);
            }

            String codes = ctx.splitExpressionsWithCurrentInputs(
                    listCode,
                    "valueIn",
                    List.of(
                            Tuple2.of(javaDataType, valueArg),
                            Tuple2.of(CodeGeneratorUtils.JAVA_BYTE, tmpResult)
                    ),
                    CodeGeneratorUtils.JAVA_BYTE,
                    body -> CodeGeneratorUtils.template(
                            """
                                    do {
                                      ${body}
                                    } while (false);
                                    return ${tmpResult};
                                    """,
                            Map.of("body", body, "tmpResult", tmpResult)
                    ),
                    funcCalls -> funcCalls.stream().map(funcCall ->
                            CodeGeneratorUtils.template(
                                    """
                                            ${tmpResult} = ${funcCall};
                                            if (${tmpResult} == ${matched}) {
                                              continue;
                                            }
                                            """,
                                    Map.ofEntries(
                                            Map.entry("tmpResult", tmpResult),
                                            Map.entry("funcCall", funcCall),
                                            Map.entry("matched", MATCHED)
                                    )
                            )).collect(Collectors.joining("\n"))
            );

            return ev.copy(Block.block(
                    """
                            ${valueCode}
                            byte ${tmpResult} = ${hasNull};
                            if (!${valueIsNull}) {
                              ${tmpResult} = ${notMatched};
                              ${javaDataType} ${valueArg} = ${valueEval};
                              do {
                                ${codes}
                              } while (false);
                            }
                            final boolean ${isNull} = (${tmpResult} == ${hasNull});
                            final boolean ${value} = (${tmpResult} == ${matched});
                            """,
                    Map.ofEntries(
                            Map.entry("valueCode", valueGen.code),
                            Map.entry("tmpResult", tmpResult),
                            Map.entry("hasNull", HAS_NULL),
                            Map.entry("valueIsNull", valueGen.isNull),
                            Map.entry("notMatched", NOT_MATCHED),
                            Map.entry("javaDataType", javaDataType),
                            Map.entry("valueArg", valueArg),
                            Map.entry("valueEval", valueGen.value),
                            Map.entry("codes", codes),
                            Map.entry("isNull", ev.isNull),
                            Map.entry("value", ev.value),
                            Map.entry("matched", MATCHED)
                    )
            ));
        }
    }

    @Override
    public String sql() {
        String valueSQL = value.sql();
        String listSQL = list.stream().map(Expression::sql).collect(Collectors.joining(", "));
        return "(" + valueSQL + " IN (" + listSQL + "))";
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new In(newChildren.get(0), newChildren.subList(1, newChildren.size()).stream().toList());
    }
}
