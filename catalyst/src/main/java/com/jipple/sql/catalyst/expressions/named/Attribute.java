package com.jipple.sql.catalyst.expressions.named;

import com.jipple.sql.catalyst.expressions.LeafExpression;

import java.util.List;


public abstract class Attribute extends LeafExpression implements NamedExpression {
    public abstract Attribute withQualifier(List<String> newQualifier);

    public abstract Attribute withName(String newName);

    public abstract Attribute withExprId(ExprId newExprId);

    @Override
    public Attribute toAttribute() {
        return this;
    }

    public abstract Attribute newInstance();
}
