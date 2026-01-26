package com.jipple.sql.catalyst.expressions.nvl;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.expressions.ComplexTypeMergingExpression;
import com.jipple.sql.catalyst.expressions.ConditionalExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.catalyst.expressions.codegen.JavaCode;
import com.jipple.sql.catalyst.util.TypeUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Coalesce extends ComplexTypeMergingExpression implements ConditionalExpression {
    private final List<Expression> children;
    public Coalesce(List<Expression> children) {
        this.children = children;
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
        return children.stream().allMatch(Expression::nullable);
    }

    @Override
    public boolean foldable() {
        return ConditionalExpression.conditionalFoldable(this);
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        if (children.isEmpty()) {
            return TypeCheckResult.typeCheckFailure(prettyName() +  "requires at least one argument");
        } else {
            return TypeUtils.checkForSameTypeInputExpr(children().stream().map(Expression::dataType).collect(Collectors.toList()), prettyName());
        }
    }

    @Override
    public Object eval(InternalRow input) {
        List<Expression> children = children();
        for (int i = 0; i < children.size(); i++) {
            Object result = children.get(i).eval(input);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        ev.isNull = JavaCode.isNullGlobal(ctx.addMutableState(CodeGeneratorUtils.JAVA_BOOLEAN, ev.isNull.toString()));

        List<String> evals = children().stream()
                .map(expression -> {
                    ExprCode eval = expression.genCode(ctx);
                    return CodeGeneratorUtils.template(
                            """
                                    ${evalCode}
                                    if (!${evalIsNull}) {
                                      ${isNull} = false;
                                      ${value} = ${evalValue};
                                      continue;
                                    }
                                    """,
                            Map.of(
                                    "evalCode", eval.code,
                                    "evalIsNull", eval.isNull,
                                    "isNull", ev.isNull,
                                    "value", ev.value,
                                    "evalValue", eval.value
                            )
                    );
                })
                .collect(Collectors.toList());

        String resultType = CodeGeneratorUtils.javaType(dataType());
        String codes = ctx.splitExpressionsWithCurrentInputs(
                evals,
                "coalesce",
                List.of(),
                resultType,
                func -> CodeGeneratorUtils.template(
                        """
                                ${resultType} ${value} = ${defaultValue};
                                do {
                                  ${func}
                                } while (false);
                                return ${value};
                                """,
                        Map.of(
                                "resultType", resultType,
                                "value", ev.value,
                                "defaultValue", CodeGeneratorUtils.defaultValue(dataType()),
                                "func", func
                        )
                ),
                funcCalls -> funcCalls.stream()
                        .map(funcCall -> CodeGeneratorUtils.template(
                                """
                                        ${value} = ${funcCall};
                                        if (!${isNull}) {
                                          continue;
                                        }
                                        """,
                                Map.of(
                                        "value", ev.value,
                                        "funcCall", funcCall,
                                        "isNull", ev.isNull
                                )
                        ))
                        .collect(Collectors.joining())
        );

        return ev.copy(Block.block(
                """
                        ${isNull} = true;
                        ${resultType} ${value} = ${defaultValue};
                        do {
                          ${codes}
                        } while (false);
                        """,
                Map.of(
                        "isNull", ev.isNull,
                        "resultType", resultType,
                        "value", ev.value,
                        "defaultValue", CodeGeneratorUtils.defaultValue(dataType()),
                        "codes", codes
                )
        ));
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new Coalesce(newChildren);
    }
}
