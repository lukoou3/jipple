package com.jipple.sql.catalyst.plans.logical;

public class OneRowRelation extends LeafNode {
    @Override
    public Object[] args() {
        return new Object[0];
    }
}
