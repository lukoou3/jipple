package com.jipple.sql.catalyst.plans.logical;

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
}
