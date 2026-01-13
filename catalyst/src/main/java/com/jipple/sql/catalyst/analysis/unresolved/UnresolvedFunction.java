package com.jipple.sql.catalyst.analysis.unresolved;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.types.DataType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnresolvedFunction extends Expression {
    public final List<String> nameParts;
    public final List<Expression> arguments;
    public final boolean isDistinct;
    public final Optional<Expression> filter;

    public UnresolvedFunction(List<String> nameParts, List<Expression> arguments, boolean isDistinct) {
        this(nameParts, arguments, isDistinct, Optional.empty());
    }

    public UnresolvedFunction(List<String> nameParts, List<Expression> arguments, boolean isDistinct, Optional<Expression> filter) {
        this.nameParts = nameParts;
        this.arguments = arguments;
        this.isDistinct = isDistinct;
        this.filter = filter;
    }

    @Override
    public Object[] args() {
        return new Object[]{nameParts, arguments, isDistinct, filter};
    }

    @Override
    public List<Expression> children() {
        return Stream.concat(arguments.stream(), filter.stream()).collect(Collectors.toList());
    }

    @Override
    public boolean resolved() {
        return false;
    }

    @Override
    public boolean nullable() {
        throw new UnresolvedException("dataType");
    }

    @Override
    public DataType dataType() {
        throw new UnresolvedException("dataType");
    }

    @Override
    public Object eval(InternalRow input) {
        throw new UnresolvedException("eval");
    }

    @Override
    public String prettyName() {
        return nameParts.stream().map(x -> x.contains(".")? "`" + x + "`" : x).collect(Collectors.joining("."));
    }

    @Override
    public String toString() {
        String distinct = isDistinct ? "distinct " : "";
        return String.format("'%s(%s%s)", prettyName(), distinct, children().stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    @Override
    protected Expression withNewChildrenInternal(List<Expression> newChildren) {
        if (filter.isPresent()) {
            return new UnresolvedFunction(nameParts, newChildren.stream().limit(newChildren.size() - 1).collect(Collectors.toList()), isDistinct, Optional.of(newChildren.get(newChildren.size() - 1)));
        } else {
            return new UnresolvedFunction(nameParts, newChildren, isDistinct);
        }
    }
}
