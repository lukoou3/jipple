package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.condition.CaseWhen;
import com.jipple.sql.types.DataType;
import com.jipple.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;

import static com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion.*;

/**
 * Coerces the type of different branches of a CASE WHEN statement to a common type.
 */
public class CaseWhenCoercion extends TypeCoercionRule {
    @Override
    public Expression transform(Expression e) {
        if (e instanceof CaseWhen c && c.childrenResolved()) {
            List<DataType> inputTypes = c.inputTypesForMerging();
            if (!haveSameType(inputTypes)) {
                Option<DataType> commonType = findWiderCommonType(inputTypes);
                return commonType.map(type -> {
                    List<Tuple2<Expression, Expression>> newBranches = new ArrayList<>(c.branches.size());
                    for (Tuple2<Expression, Expression> branch : c.branches) {
                        newBranches.add(Tuple2.of(branch._1, castIfNotSameType(branch._2, type)));
                    }
                    Option<Expression> newElseValue = c.elseValue.map(value -> castIfNotSameType(value, type));
                    return new CaseWhen(newBranches, newElseValue);
                }).getOrElse(c);
            }
        }
        return e;
    }
}
