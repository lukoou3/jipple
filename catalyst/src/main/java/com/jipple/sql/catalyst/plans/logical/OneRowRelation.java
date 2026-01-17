package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.named.Attribute;

import java.util.List;

public class OneRowRelation extends LeafNode {
    @Override
    public Object[] args() {
        return new Object[0];
    }

    @Override
    public List<Attribute> output() {
        return List.of();
    }
}
