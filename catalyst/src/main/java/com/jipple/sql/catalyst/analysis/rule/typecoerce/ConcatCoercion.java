package com.jipple.sql.catalyst.analysis.rule.typecoerce;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.collection.Concat;
import com.jipple.sql.types.BinaryType;

import java.util.List;
import java.util.stream.Collectors;

import static com.jipple.sql.types.DataTypes.STRING;

/**
 * Coerces the types of {@link Concat} children to expected ones.
 *
 * If `spark.sql.function.concatBinaryAsString` is false and all children types are binary,
 * the expected types are binary. Otherwise, the expected ones are strings.
 */
public class ConcatCoercion extends TypeCoercionRule {
    @Override
    public Expression transform(Expression e) {
        if (e instanceof Concat c) {
            List<Expression> children = c.children();
            if (!c.childrenResolved() || children.isEmpty()) {
                return c;
            }
            boolean allBinary = children.stream().allMatch(child -> child.dataType() instanceof BinaryType);
            if (!allBinary) {
                List<Expression> newChildren = children.stream()
                        .map(child -> TypeCoercion.implicitCast(child, STRING).getOrElse(child))
                        .collect(Collectors.toList());
                return c.withNewChildren(newChildren);
            }
        }
        return e;
    }
}
