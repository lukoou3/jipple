package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.BinaryOperator;
import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.plans.logical.LogicalPlan;
import com.jipple.sql.types.AbstractDataType;
import com.jipple.sql.types.DataType;
import com.jipple.sql.types.DecimalType;
import com.jipple.sql.types.NullType;

import java.util.ArrayList;
import java.util.List;

public class ImplicitTypeCasts extends TypeCoercionRule {

    private boolean canHandleTypeCoercion(DataType leftType, DataType rightType) {
        if (leftType instanceof DecimalType && rightType instanceof NullType) {
            return true;
        }
        if (leftType instanceof NullType&& rightType instanceof DecimalType) {
            return true;
        }
        // If DecimalType operands are involved except for the two cases above,
        // DecimalPrecision will handle it.
        return !(leftType instanceof DecimalType) && !(rightType instanceof DecimalType) && !leftType.equals(rightType);
    }

    @Override
    public Expression transform(Expression e) {
        // Skip nodes who's children have not been resolved yet.
        if (!e.childrenResolved()) {
            return e;
        }
        if (e instanceof BinaryOperator b) {
            Expression left = b.left;
            Expression right = b.right;
            if (canHandleTypeCoercion(left.dataType(), right.dataType())) {
                Option<DataType> commonType = TypeCoercion.findTightestCommonType(
                        left.dataType(), right.dataType());
                if (commonType.isDefined()) {
                    DataType targetType = commonType.get();
                    if (b.inputType().acceptsType(targetType)) {
                        Expression newLeft = left.dataType().equals(targetType)
                                ? left
                                : new Cast(left, targetType);
                        Expression newRight = right.dataType().equals(targetType)
                                ? right
                                : new Cast(right, targetType);
                        return b.withNewChildren(java.util.List.of(newLeft, newRight));
                    }
                    return b;
                }
                return b;
            }
        }
        if (e.expectsInputTypes().isDefined()) {
            List<Expression> children = e.children();
            List<AbstractDataType> inputTypes = e.expectsInputTypes().get();
            if (children.size() != inputTypes.size()) {
                throw new IllegalStateException(
                        "expectsInputTypes size " + inputTypes.size()
                                + " does not match children size " + children.size()
                                + " for " + e.getClass().getName());
            }
            List<Expression> newChildren = new ArrayList<>(children.size());
            for (int i = 0; i < children.size(); i++) {
                Expression in = children.get(i);
                AbstractDataType expected = inputTypes.get(i);
                // If we cannot do the implicit cast, just use the original input.
                Expression casted = TypeCoercion.implicitCast(in, expected).getOrElse(in);
                newChildren.add(casted);
            }
            return e.withNewChildren(newChildren);
        }

        return e;
    }

}
