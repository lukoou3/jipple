package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.condition.If;
import com.jipple.sql.types.NullType;

import static com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion.*;
import static com.jipple.sql.types.DataTypes.BOOLEAN;

/**
 * Coerces the type of different branches of If statement to a common type.
 */
public class IfCoercion extends TypeCoercionRule {

    @Override
    public Expression transform(Expression e) {
        if (!e.childrenResolved()) {
            return e;
        }
        if (e instanceof If i) {
            Expression pred = i.predicate;
            Expression left = i.trueValue;
            Expression right = i.falseValue;
            if (!haveSameType(i.inputTypesForMerging())) {
                return findWiderTypeForTwo(left.dataType(), right.dataType())
                        .map(widestType -> {
                            Expression newLeft = castIfNotSameType(left, widestType);
                            Expression newRight = castIfNotSameType(right, widestType);
                            return new If(pred, newLeft, newRight);
                        })
                        .getOrElse(i);
            }
            if (pred instanceof Literal literal && literal.value == null && literal.dataType instanceof NullType) {
                return new If(Literal.of(null, BOOLEAN), left, right);
            }
            if (pred.dataType() instanceof NullType) {
                return new If(new Cast(pred, BOOLEAN), left, right);
            }
        }
        return e;
    }
}
