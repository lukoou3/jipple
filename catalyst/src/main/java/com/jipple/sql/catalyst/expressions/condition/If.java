package com.jipple.sql.catalyst.expressions.condition;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion;
import com.jipple.sql.catalyst.expressions.ComplexTypeMergingExpression;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.codegen.Block;
import com.jipple.sql.catalyst.expressions.codegen.CodeGeneratorUtils;
import com.jipple.sql.catalyst.expressions.codegen.CodegenContext;
import com.jipple.sql.catalyst.expressions.codegen.ExprCode;
import com.jipple.sql.types.DataType;

import java.util.List;
import java.util.Map;

import static com.jipple.sql.types.DataTypes.BOOLEAN;

public class If extends ComplexTypeMergingExpression {
    public final Expression predicate;
    public final Expression trueValue;
    public final Expression falseValue;

    public If(Expression predicate, Expression trueValue, Expression falseValue) {
        this.predicate = predicate;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    public Object[] args() {
        return new Object[] { predicate, trueValue, falseValue };
    }

    @Override
    public List<Expression> children() {
        return List.of(predicate, trueValue, falseValue);
    }

    @Override
    public List<DataType> inputTypesForMerging() {
        return List.of(trueValue.dataType(), falseValue.dataType());
    }

    @Override
    public boolean nullable() {
        return trueValue.nullable() || falseValue.nullable();
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        if (!predicate.dataType().equals(BOOLEAN)) {
            return TypeCheckResult.typeCheckFailure("type of predicate expression in If should be boolean");
        } else if (!TypeCoercion.haveSameType(inputTypesForMerging())) {
            return TypeCheckResult.typeCheckFailure(String.format("differing types:%s and %s", trueValue.dataType(), falseValue.dataType()));
        } else {
            return TypeCheckResult.typeCheckSuccess();
        }
    }

    @Override
    public Object eval(InternalRow input) {
        if (java.lang.Boolean.TRUE.equals(predicate.eval(input))) {
            return trueValue.eval(input);
        } else {
            return falseValue.eval(input);
        }
    }

    @Override
    protected ExprCode doGenCode(CodegenContext ctx, ExprCode ev) {
        ExprCode condEval = predicate.genCode(ctx);
        ExprCode trueEval = trueValue.genCode(ctx);
        ExprCode falseEval = falseValue.genCode(ctx);

        Block code = Block.block(
                """
                        ${condCode}
                        boolean ${isNull} = false;
                        ${javaType} ${value} = ${defaultValue};
                        if (!${condIsNull} && ${condValue}) {
                          ${trueCode}
                          ${isNull} = ${trueIsNull};
                          ${value} = ${trueValue};
                        } else {
                          ${falseCode}
                          ${isNull} = ${falseIsNull};
                          ${value} = ${falseValue};
                        }
                        """,
                Map.ofEntries(
                        Map.entry("condCode", condEval.code),
                        Map.entry("isNull", ev.isNull),
                        Map.entry("javaType", CodeGeneratorUtils.javaType(dataType())),
                        Map.entry("value", ev.value),
                        Map.entry("defaultValue", CodeGeneratorUtils.defaultValue(dataType())),
                        Map.entry("condIsNull", condEval.isNull),
                        Map.entry("condValue", condEval.value),
                        Map.entry("trueCode", trueEval.code),
                        Map.entry("trueIsNull", trueEval.isNull),
                        Map.entry("trueValue", trueEval.value),
                        Map.entry("falseCode", falseEval.code),
                        Map.entry("falseIsNull", falseEval.isNull),
                        Map.entry("falseValue", falseEval.value)
                )
        );

        return ev.copy(code);
    }

    @Override
    public String toString() {
        return String.format("if (%s) %s else %s", predicate, trueValue, falseValue);
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        return new If(newChildren.get(0), newChildren.get(1), newChildren.get(2));
    }
}
