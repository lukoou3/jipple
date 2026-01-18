package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.nvl.IsNotNull;
import com.jipple.sql.catalyst.expressions.predicate.And;
import com.jipple.sql.catalyst.expressions.predicate.EqualNullSafe;
import com.jipple.sql.catalyst.expressions.predicate.EqualTo;
import com.jipple.sql.catalyst.expressions.predicate.Not;
import com.jipple.sql.types.BooleanType;
import com.jipple.sql.types.Decimal;
import com.jipple.sql.types.NumericType;

import java.util.List;

/**
 * Changes numeric values to booleans so that expressions like true = 1 can be evaluated.
 */
public class BooleanEquality extends TypeCoercionRule {
    private static final List<Object> TRUE_VALUES = List.of(Byte.valueOf((byte) 1), Short.valueOf((short) 1), Integer.valueOf(1), Long.valueOf(1L), Decimal.ONE);
    private static final List<Object> FALSE_VALUES = List.of(Byte.valueOf((byte) 0), Short.valueOf((short) 0), Integer.valueOf(0), Long.valueOf(0L), Decimal.ZERO);

    @Override
    public Expression transform(Expression e) {
        if (!e.childrenResolved()) {
            return e;
        }

        if (e instanceof EqualTo eq) {
            Expression left = eq.left;
            Expression right = eq.right;

            Expression simplified = simplifyBooleanNumericEquality(left, right, false);
            if (simplified != null) {
                return simplified;
            }

            if (isBooleanType(left) && isNumericType(right)) {
                return new EqualTo(new Cast(left, right.dataType()), right);
            }
            if (isNumericType(left) && isBooleanType(right)) {
                return new EqualTo(left, new Cast(right, left.dataType()));
            }
        }

        if (e instanceof EqualNullSafe eq) {
            Expression left = eq.left;
            Expression right = eq.right;

            Expression simplified = simplifyBooleanNumericEquality(left, right, true);
            if (simplified != null) {
                return simplified;
            }

            if (isBooleanType(left) && isNumericType(right)) {
                return new EqualNullSafe(new Cast(left, right.dataType()), right);
            }
            if (isNumericType(left) && isBooleanType(right)) {
                return new EqualNullSafe(left, new Cast(right, left.dataType()));
            }
        }

        return e;
    }

    private Expression simplifyBooleanNumericEquality(Expression left, Expression right, boolean nullSafe) {
        if (isBooleanType(left) && right instanceof Literal literal && isNumericType(right)) {
            Object value = literal.value;
            if (TRUE_VALUES.contains(value)) {
                return nullSafe ? new And(new IsNotNull(left), left) : left;
            }
            if (FALSE_VALUES.contains(value)) {
                Expression negated = new Not(left);
                return nullSafe ? new And(new IsNotNull(left), negated) : negated;
            }
        }
        if (left instanceof Literal literal && isNumericType(left) && isBooleanType(right)) {
            Object value = literal.value;
            if (TRUE_VALUES.contains(value)) {
                return nullSafe ? new And(new IsNotNull(right), right) : right;
            }
            if (FALSE_VALUES.contains(value)) {
                Expression negated = new Not(right);
                return nullSafe ? new And(new IsNotNull(right), negated) : negated;
            }
        }
        return null;
    }

    private boolean isBooleanType(Expression expression) {
        return expression.dataType() instanceof BooleanType;
    }

    private boolean isNumericType(Expression expression) {
        return expression.dataType() instanceof NumericType;
    }

}
