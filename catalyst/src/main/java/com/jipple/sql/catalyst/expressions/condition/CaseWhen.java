package com.jipple.sql.catalyst.expressions.condition;

import com.google.common.base.Preconditions;
import com.jipple.collection.Option;
import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion;
import com.jipple.sql.catalyst.expressions.ComplexTypeMergingExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.JavaCode;
import com.jipple.sql.types.DataType;
import com.jipple.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class CaseWhen extends ComplexTypeMergingExpression {
    public final List<Tuple2<Expression, Expression>> branches;
    public final Option<Expression> elseValue;

    public CaseWhen(List<Tuple2<Expression, Expression>> branches, Option<Expression> elseValue) {
        this.branches = branches;
        this.elseValue = elseValue;
    }

    public CaseWhen(List<Tuple2<Expression, Expression>> branches) {
        this(branches, Option.none());
    }

    @Override
    public Object[] args() {
        return new Object[] { branches, elseValue };
    }

    @Override
    public List<Expression> children() {
        List<Expression> children = new ArrayList<>(branches.size() * 2 + (elseValue.isDefined() ? 1 : 0 ));
        for (Tuple2<Expression, Expression> branch : branches) {
            children.add(branch._1);
            children.add(branch._2);
        }
        if (elseValue.isDefined()) {
            children.add(elseValue.get());
        }
        return children;
    }

    @Override
    public List<DataType> inputTypesForMerging() {
        List<DataType> inputTypes = new ArrayList<>();
        for (Tuple2<Expression, Expression> branch : branches) {
            inputTypes.add(branch._2.dataType());
        }
        if (elseValue.isDefined()) {
            inputTypes.add(elseValue.get().dataType());
        }
        return inputTypes;
    }

    @Override
    public boolean nullable() {
        return branches.stream().anyMatch(x -> x._2.nullable()) || elseValue.map(x -> x.nullable()).getOrElse(true);
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        if (TypeCoercion.haveSameType(inputTypesForMerging())) {
            // Make sure all branch conditions are boolean types.
            for (int i = 0; i < branches.size(); i++) {
                if (!branches.get(i)._1.dataType().equals(BOOLEAN)) {
                    return TypeCheckResult.typeCheckFailure("type of predicate expression in CaseWhen should be boolean, but find not :" + branches.get(i)._1.sql());
                }
            }
            return TypeCheckResult.typeCheckSuccess();
        } else {
            return TypeCheckResult.typeCheckFailure("differing types in CaseWhen:" + inputTypesForMerging().stream().map(x -> x.sql()).collect(Collectors.joining(", ")));
        }
    }

    @Override
    public Object eval(InternalRow input) {
        Tuple2<Expression, Expression> branche;
        for (int i = 0; i < branches.size(); i ++) {
            branche = branches.get(i);
            if (Boolean.TRUE.equals(branche._1.eval(input))) {
                return branche._2.eval(input);
            }
        }
        if (elseValue.isDefined()) {
            return elseValue.get().eval(input);
        } else {
            return null;
        }
    }

    private ExprCode multiBranchesCodegen(CodegenContext ctx, ExprCode ev) {
        // This variable holds the state of the result:
        // -1 means the condition is not met yet and the result is unknown.
        final int NOT_MATCHED = -1;
        // 0 means the condition is met and result is not null.
        final int HAS_NONNULL = 0;
        // 1 means the condition is met and result is null.
        final int HAS_NULL = 1;
        // It is initialized to `NOT_MATCHED`, and if it's set to `HAS_NULL` or `HAS_NONNULL`,
        // We won't go on anymore on the computation.
        String resultState = ctx.freshName("caseWhenResultState");
        ev.value = JavaCode.global(
                ctx.addMutableState(CodeGeneratorUtils.javaType(dataType()), ev.value.toString()),
                dataType());

        // these blocks are meant to be inside a
        // do {
        //   ...
        // } while (false);
        // loop
        List<String> cases = new ArrayList<>(branches.size());
        for (Tuple2<Expression, Expression> branch : branches) {
            ExprCode cond = branch._1.genCode(ctx);
            ExprCode res = branch._2.genCode(ctx);
            cases.add(CodeGeneratorUtils.template(
                    """
                            ${condCode}
                            if (!${condIsNull} && ${condValue}) {
                              ${resCode}
                              ${resultState} = (byte)(${resIsNull} ? ${HAS_NULL} : ${HAS_NONNULL});
                              ${value} = ${resValue};
                              continue;
                            }
                            """,
                    Map.ofEntries(
                            Map.entry("condCode", cond.code),
                            Map.entry("condIsNull", cond.isNull),
                            Map.entry("condValue", cond.value),
                            Map.entry("resCode", res.code),
                            Map.entry("resultState", resultState),
                            Map.entry("resIsNull", res.isNull),
                            Map.entry("HAS_NULL", HAS_NULL),
                            Map.entry("HAS_NONNULL", HAS_NONNULL),
                            Map.entry("value", ev.value),
                            Map.entry("resValue", res.value)
                    )
            ));
        }

        List<String> allConditions = new ArrayList<>(cases.size() + (elseValue.isDefined() ? 1 : 0));
        allConditions.addAll(cases);
        if (elseValue.isDefined()) {
            ExprCode res = elseValue.get().genCode(ctx);
            allConditions.add(CodeGeneratorUtils.template(
                    """
                            ${resCode}
                            ${resultState} = (byte)(${resIsNull} ? ${HAS_NULL} : ${HAS_NONNULL});
                            ${value} = ${resValue};
                            """,
                    Map.ofEntries(
                            Map.entry("resCode", res.code),
                            Map.entry("resultState", resultState),
                            Map.entry("resIsNull", res.isNull),
                            Map.entry("HAS_NULL", HAS_NULL),
                            Map.entry("HAS_NONNULL", HAS_NONNULL),
                            Map.entry("value", ev.value),
                            Map.entry("resValue", res.value)
                    )
            ));
        }

        String codes = ctx.splitExpressionsWithCurrentInputs(
                allConditions,
                "caseWhen",
                List.of(),
                CodeGeneratorUtils.JAVA_BYTE,
                func -> CodeGeneratorUtils.template(
                        """
                                ${javaByte} ${resultState} = ${notMatched};
                                do {
                                  ${func}
                                } while (false);
                                return ${resultState};
                                """,
                        Map.ofEntries(
                                Map.entry("javaByte", CodeGeneratorUtils.JAVA_BYTE),
                                Map.entry("resultState", resultState),
                                Map.entry("notMatched", NOT_MATCHED),
                                Map.entry("func", func)
                        )
                ),
                funcCalls -> funcCalls.stream()
                        .map(funcCall -> CodeGeneratorUtils.template(
                                """
                                        ${resultState} = ${funcCall};
                                        if (${resultState} != ${notMatched}) {
                                          continue;
                                        }
                                        """,
                                Map.ofEntries(
                                        Map.entry("resultState", resultState),
                                        Map.entry("funcCall", funcCall),
                                        Map.entry("notMatched", NOT_MATCHED)
                                )
                        ))
                        .collect(Collectors.joining())
        );

        return ev.copy(Block.block(
                """
                        ${javaByte} ${resultState} = ${notMatched};
                        do {
                          ${codes}
                        } while (false);
                        // TRUE if any condition is met and the result is null, or no any condition is met.
                        final boolean ${isNull} = (${resultState} != ${hasNonNull});
                        """,
                Map.ofEntries(
                        Map.entry("javaByte", CodeGeneratorUtils.JAVA_BYTE),
                        Map.entry("resultState", resultState),
                        Map.entry("notMatched", NOT_MATCHED),
                        Map.entry("codes", codes),
                        Map.entry("isNull", ev.isNull),
                        Map.entry("hasNonNull", HAS_NONNULL)
                )
        ));
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        if (branches.size() == 1) {
            // If we have only single branch we can use If expression and its codeGen
            return new If(
                    branches.get(0)._1,
                    branches.get(0)._2,
                    elseValue.getOrElse(Literal.of(null, branches.get(0)._2.dataType()))
            ).doGenCode(ctx, ev);
        } else {
            return multiBranchesCodegen(ctx, ev);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("case");
        for (int i = 0; i < branches.size(); i ++) {
            sb.append(" when ").append(branches.get(i)._1).append(" then ").append(branches.get(i)._2);
        }
        if (elseValue.isDefined()) {
            sb.append(" else ").append(elseValue.get());
        }
        sb.append(" end");
        return sb.toString();
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        Preconditions.checkArgument(newChildren.size() == branches.size() * 2 + (elseValue.isDefined() ? 1 : 0));
        List<Tuple2<Expression, Expression>> newBranches = new ArrayList<>(branches.size());
        for (int i = 0; i < branches.size(); i += 2) {
            newBranches.add(Tuple2.of(newChildren.get(i), newChildren.get(i + 1)));
        }
        return new CaseWhen(newBranches, elseValue.isDefined()? Option.of(newChildren.get(newChildren.size() - 1)) : Option.empty());
    }
}
