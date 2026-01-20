package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.expressions.named.Attribute;
import com.jipple.sql.catalyst.expressions.named.AttributeReference;

import java.util.List;
import java.util.stream.Collectors;

public final class BindReferences {
    private BindReferences() {
    }

    public static <A extends Expression> A bindReference(A expression, AttributeSeq input) {
        return bindReference(expression, input, false);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Expression> A bindReference(A expression, AttributeSeq input, boolean allowFailures) {
        Expression transformed = expression.transformDown(e -> {
            if (e instanceof AttributeReference a) {
                int ordinal = input.indexOf(a.exprId());
                if (ordinal == -1) {
                    if (allowFailures) {
                        return e;
                    }
                    String attrs = input.attrs()
                            .stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", ", "[", "]"));
                    throw new IllegalStateException("Couldn't find " + a + " in " + attrs);
                }
                Attribute boundAttr = input.apply(ordinal);
                return new BoundReference(ordinal, a.dataType(), boundAttr.nullable());
            }
            return e;
        });
        return (A) transformed;
    }

    /** A helper function to bind given expressions to an input schema. */
    public static <A extends Expression> List<A> bindReferences(List<A> expressions, AttributeSeq input) {
        return expressions.stream()
                .map(expr -> BindReferences.bindReference(expr, input, false))
                .collect(Collectors.toList());
    }

}
