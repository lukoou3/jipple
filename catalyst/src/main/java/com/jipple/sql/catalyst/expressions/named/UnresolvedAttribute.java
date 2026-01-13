package com.jipple.sql.catalyst.expressions.named;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.unresolved.UnresolvedException;
import com.jipple.sql.types.DataType;

import java.util.List;
import java.util.stream.Collectors;

public class UnresolvedAttribute extends Attribute {
    public final List<String> nameParts;

    public UnresolvedAttribute(List<String> nameParts) {
        this.nameParts = nameParts;
    }

    @Override
    public String name() {
        return nameParts.stream().map(n -> n.contains(".") ? "`" + n + "`" : n).collect(Collectors.joining("."));
    }

    /** Unevaluable is not foldable because we don't have an eval for it. */
    @Override
    public boolean foldable() {
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
    public Object[] args() {
        return new Object[] { nameParts };
    }

    @Override
    public String toString() {
        return "'" + name();
    }

    public static UnresolvedAttribute quoted(String name) {
        return new UnresolvedAttribute(List.of(name));
    }
}
