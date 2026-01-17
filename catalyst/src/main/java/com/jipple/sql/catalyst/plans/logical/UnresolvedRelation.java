package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.named.Attribute;

import java.util.List;

public class UnresolvedRelation extends LeafNode {
    public final List<String> multipartIdentifier;

    public UnresolvedRelation(List<String> multipartIdentifier) {
        this.multipartIdentifier = multipartIdentifier;
    }

    @Override
    public Object[] args() {
        return new Object[] { multipartIdentifier };
    }

    @Override
    public boolean resolved() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public List<Attribute> output() {
        return List.of();
    }
}
