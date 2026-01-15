package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.AliasIdentifier;

public class SubqueryAlias extends UnaryNode {
    public final AliasIdentifier identifier;

    public SubqueryAlias(AliasIdentifier identifier, LogicalPlan child) {
        super(child);
        this.identifier = identifier;
    }

    public SubqueryAlias(String identifier, LogicalPlan child) {
        this(new AliasIdentifier(identifier), child);
    }

    @Override
    public Object[] args() {
        return new Object[] { identifier, child };
    }

    @Override
    public LogicalPlan withNewChildInternal(LogicalPlan newChild) {
        return new SubqueryAlias(identifier, newChild);
    }
}
