package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.named.Attribute;

import java.util.List;

public class RelationPlaceholder extends LeafNode  {
    public final List<Attribute> output;
    public final String name;

    public RelationPlaceholder(List<Attribute> output, String name) {
        this.output = output;
        this.name = name;
    }

    @Override
    public Object[] args() {
        return new Object[]{output, name};
    }

    @Override
    public List<Attribute> output() {
        return output;
    }
}
