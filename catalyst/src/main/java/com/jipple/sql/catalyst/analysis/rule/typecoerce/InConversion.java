package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.collection.Option;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.predicate.In;
import com.jipple.sql.types.DataType;

import java.util.List;
import java.util.stream.Collectors;

import static com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion.castIfNotEquals;
import static com.jipple.sql.catalyst.analysis.rule.typecoerce.TypeCoercion.findWiderCommonType;

/**
 * Handles type coercion for both IN expression with subquery and IN
 * expressions without subquery.
 * 1. In the first case, find the common type by comparing the left hand side (LHS)
 *    expression types against corresponding right hand side (RHS) expression derived
 *    from the subquery expression's plan output. Inject appropriate casts in the
 *    LHS and RHS side of IN expression.
 *
 * 2. In the second case, convert the value and in list expressions to the
 *    common operator type by looking at all the argument types and finding
 *    the closest one that all the arguments can be cast to. When no common
 *    operator type is found the original expression will be returned and an
 *    Analysis Exception will be raised at the type checking phase.
 */
public class InConversion extends TypeCoercionRule {
    @Override
    public Expression transform(Expression e) {
        if (!e.childrenResolved()) {
            return e;
        }
        if (e instanceof In i) {
            Expression value = i.value;
            if (i.list.stream().anyMatch(child -> !child.dataType().equals(value.dataType()))) {
                List<DataType> dataTypes = i.children().stream().map(Expression::dataType).collect(Collectors.toList());
                Option<DataType> finalDataType = findWiderCommonType(dataTypes);
                if (finalDataType.isDefined()) {
                    DataType targetType = finalDataType.get();
                    List<Expression> newChildren = i.children().stream()
                            .map(child -> castIfNotEquals(child, targetType))
                            .collect(Collectors.toList());
                    return i.withNewChildren(newChildren);
                }
                return i;
            }
        }
        return e;
    }
}
