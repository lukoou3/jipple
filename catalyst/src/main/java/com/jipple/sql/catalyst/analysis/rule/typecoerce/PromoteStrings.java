package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.collection.Option;
import com.jipple.sql.SQLConf;
import com.jipple.sql.catalyst.expressions.Cast;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.Literal;
import com.jipple.sql.catalyst.expressions.arithmetic.BinaryArithmetic;
import com.jipple.sql.catalyst.expressions.predicate.BinaryComparison;
import com.jipple.sql.catalyst.expressions.predicate.EqualNullSafe;
import com.jipple.sql.catalyst.expressions.predicate.EqualTo;
import com.jipple.sql.types.*;

import java.util.List;

import static com.jipple.sql.types.DataTypes.DOUBLE;
import static com.jipple.sql.types.DataTypes.TIMESTAMP;

/**
 * Promotes strings that appear in arithmetic expressions.
 */
public class PromoteStrings extends TypeCoercionRule {
    private Expression castExpr(Expression expr, DataType targetType) {
        if (expr.dataType() instanceof NullType) {
            return Literal.of(null, targetType);
        }
        if (!expr.dataType().equals(targetType)) {
            return new Cast(expr, targetType);
        }
        return expr;
    }

    @Override
    public Expression transform(Expression e) {
        if (!e.childrenResolved()) {
            return e;
        }

        if (e instanceof BinaryArithmetic a) {
            Expression left = a.left;
            Expression right = a.right;
            if (left.dataType() instanceof StringType
                    && !(right.dataType() instanceof CalendarIntervalType)) {
                return a.withNewChildren(List.of(new Cast(left, DOUBLE), right));
            }
            if (right.dataType() instanceof StringType
                    && !(left.dataType() instanceof CalendarIntervalType)) {
                return a.withNewChildren(List.of(left, new Cast(right, DOUBLE)));
            }
        }

        if (e instanceof BinaryComparison p) {
            Expression left = p.left;
            Expression right = p.right;

            if ((p instanceof EqualTo || p instanceof EqualNullSafe)
                    && left.dataType() instanceof StringType
                    && right.dataType() instanceof TimestampType) {
                return p.withNewChildren(List.of(new Cast(left, TIMESTAMP), right));
            }
            if ((p instanceof EqualTo || p instanceof EqualNullSafe)
                    && left.dataType() instanceof TimestampType
                    && right.dataType() instanceof StringType) {
                return p.withNewChildren(List.of(left, new Cast(right, TIMESTAMP)));
            }

            Option<DataType> commonType = TypeCoercion.findCommonTypeForBinaryComparison(
                    left.dataType(),
                    right.dataType(),
                    SQLConf.get());
            if (commonType.isDefined()) {
                DataType targetType = commonType.get();
                return p.withNewChildren(List.of(
                        castExpr(left, targetType),
                        castExpr(right, targetType)));
            }
        }

        return e;
    }
}
