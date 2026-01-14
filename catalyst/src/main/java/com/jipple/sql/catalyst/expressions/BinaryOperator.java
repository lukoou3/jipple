package com.jipple.sql.catalyst.expressions;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.analysis.TypeCheckResult;
import com.jipple.sql.types.AbstractDataType;

import java.util.List;
import java.util.Optional;

/**
 * A [[BinaryExpression]] that is an operator, with two properties:
 *
 * 1. The string representation is "x symbol y", rather than "funcName(x, y)".
 * 2. Two inputs are expected to be of the same type. If the two inputs have different types,
 *    the analyzer will find the tightest common type and do the proper type casting.
 */
public abstract class BinaryOperator extends BinaryExpression {
    public BinaryOperator(Expression left, Expression right) {
        super(left, right);
    }

    /**
     * Expected input type from both left/right child expressions, similar to the ImplicitCastInputTypes trait.
     */
    public abstract AbstractDataType inputType();

    @Override
    public Option<List<AbstractDataType>> expectsInputTypes() {
        AbstractDataType inputType = inputType();
        return Option.some(List.of(inputType, inputType));
    }

    public abstract String symbol();

    public String sqlOperator() {
        return symbol();
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, sqlOperator(), right);
    }

    @Override
    public TypeCheckResult checkInputDataTypes() {
        // First check whether left and right have the same type, then check if the type is acceptable.
        if (!left.dataType().sameType(right.dataType())) {
            return TypeCheckResult.typeCheckFailure("differing types");
        } else if (!inputType().acceptsType(left.dataType())) {
            return TypeCheckResult.typeCheckFailure("requires " + inputType().simpleString() + " type");
        } else {
            return TypeCheckResult.typeCheckSuccess();
        }
    }

}
