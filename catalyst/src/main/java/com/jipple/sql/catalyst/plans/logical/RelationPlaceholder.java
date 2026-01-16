package com.jipple.sql.catalyst.plans.logical;

public class RelationPlaceholder extends LeafNode  {
    public final String name;

    public RelationPlaceholder(String name) {
        this.name = name;
    }

    @Override
    public Object[] args() {
        return new Object[]{name};
    }
}
