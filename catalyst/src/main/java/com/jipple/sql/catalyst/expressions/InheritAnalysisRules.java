package com.jipple.sql.catalyst.expressions;

import com.jipple.sql.catalyst.trees.UnaryLike;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class InheritAnalysisRules extends RuntimeReplaceable implements UnaryLike<Expression> {
    @Override
    public Expression child() {
        return replacement();
    }

    @Override
    public List<Expression> children() {
        return List.of(child());
    }

    public abstract List<Expression> parameters();

    @Override
    protected Stream<Object> flatArguments() {
        return parameters().stream().map(x -> x);
    }

    public String makeSQLString(List<String> childrenSQL) {
        return prettyName() + childrenSQL.stream().collect(Collectors.joining(", ", "(", ")"));
    }

    @Override
    public final String sql() {
        return makeSQLString(parameters().stream().map(x -> x.sql()).collect(Collectors.toList()));
    }
    @Override
    public Expression mapChildren(Function<Expression, Expression> f) {
        return UnaryLike.mapChildren(this, f);
    }

    @Override
    protected final Expression withNewChildrenInternal(List<Expression> newChildren) {
        return UnaryLike.withNewChildrenInternal(this, newChildren);
    }

}
