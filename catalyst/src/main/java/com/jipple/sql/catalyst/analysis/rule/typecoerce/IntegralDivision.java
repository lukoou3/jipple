package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.arithmetic.IntegralDivide;
import com.jipple.sql.types.IntegerType;

import java.util.List;

import static com.jipple.sql.types.DataTypes.LONG;

/**
 * The DIV operator always returns long-type value.
 * This rule cast the integral inputs to long type, to avoid overflow during calculation.
 */
public class IntegralDivision extends TypeCoercionRule {
    @Override
    public Expression transform(Expression e) {
        if (!e.childrenResolved()) {
            return e;
        }
        if (e instanceof IntegralDivide d) {
            Expression left = mayCastToLong(d.left);
            Expression right = mayCastToLong(d.right);
            return d.withNewChildren(List.of(left, right));
        }
        return e;
    }

    private Expression mayCastToLong(Expression expr) {
        if (expr.dataType() instanceof IntegerType) {
            return new Cast(expr, LONG);
        }
        return expr;
    }
}
