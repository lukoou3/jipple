package com.jipple.sql.catalyst.expressions.named;

import com.jipple.sql.catalyst.InternalRow;
import com.jipple.sql.catalyst.analysis.unresolved.UnresolvedException;
import com.jipple.sql.catalyst.util.QuotingUtils;
import com.jipple.sql.types.DataType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AttributeReference extends Attribute {
    public final String name;
    public final DataType dataType;
    public final boolean nullable;
    public final ExprId exprId;
    public final List<String> qualifier;

    public AttributeReference(String name, DataType dataType, boolean nullable, ExprId exprId, List<String> qualifier) {
        this.name = name;
        this.dataType = dataType;
        this.nullable = nullable;
        this.exprId = exprId;
        this.qualifier = qualifier;
    }

    public AttributeReference(String name, DataType dataType, boolean nullable, List<String> qualifier) {
        this(name, dataType, nullable, ExprId.newExprId(), qualifier);
    }

    public AttributeReference(String name, DataType dataType, List<String> qualifier) {
        this(name, dataType, true, ExprId.newExprId(), qualifier);
    }

    public AttributeReference(String name, DataType dataType, boolean nullable) {
        this(name, dataType, nullable, ExprId.newExprId(), Collections.emptyList());
    }

    public AttributeReference(String name, DataType dataType) {
        this(name, dataType, true);
    }

    @Override
    public Object[] args() {
        return new Object[]{name, dataType, nullable, exprId, qualifier} ;
    }

    @Override
    public boolean foldable() {
        return false;
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

    @Override
    public DataType dataType() {
        return dataType;
    }

    @Override
    public Object eval(InternalRow input) {
        throw new UnresolvedException("eval");
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ExprId exprId() {
        return exprId;
    }

    @Override
    public List<String> qualifier() {
        return qualifier;
    }

    @Override
    public AttributeReference withQualifier(List<String> newQualifier) {
        if (newQualifier.equals(qualifier)) {
            return this;
        } else {
            return new AttributeReference(name, dataType, nullable, exprId, newQualifier);
        }
    }

    @Override
    public AttributeReference withName(String newName) {
        return name.equals(newName)? this : new AttributeReference(newName, dataType, nullable, exprId, qualifier);
    }

    @Override
    public AttributeReference withExprId(ExprId newExprId) {
        if (exprId.equals(newExprId)) {
            return this;
        } else {
            return new AttributeReference(name, dataType, nullable, newExprId, qualifier);
        }
    }

    @Override
    public AttributeReference newInstance() {
        return new AttributeReference(name, dataType, nullable, qualifier);
    }

    /** Used to signal the column used to calculate an eventTime watermark (e.g. a#1-T{delayMs}) */
    private String delaySuffix() {
        return "";
    }

    @Override
    public String toString() {
        return name + "#" + exprId.id + typeSuffix() + delaySuffix();
    }

    // Since the expression id is not in the first constructor it is missing from the default
    // tree string.
    @Override
    public String simpleString(int maxFields) {
        return name + "#" + exprId.id + ": " + dataType.simpleString();
    }

    @Override
    public String sql() {
        StringBuilder sb = new StringBuilder();
        for (String s : qualifier) {
            sb.append(QuotingUtils.quoteIfNeeded(s)).append('.');
        }
        sb.append(QuotingUtils.quoteIfNeeded(name));
        return sb.toString();
    }
}
